package org.example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.containershipPb.Generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class ModelisationAvecSupport {
    /*
     * On prend un exemple d'un navire avec 2 baies, 2 blocs par baie, 2 piles par bloc et au maximum 2 container au-dessus et 2 en-dessous du pont
     * La plannification de chargement est générée aléatoirement à chaque exécution
     */

    static int nbCont = 40, nbStop = 4, nbBaies = 2, nbBlocs = 2, nbPiles = 2, hMax = 4;
    static int maxPos = (nbBaies - 1) * 1000 + (nbBlocs - 1) * 100 + (nbPiles - 1) * 10 + hMax + 1;
    static int[][] planification;

    public static void main(String[] args) throws IOException {
        Model model = new Model("Chargement navire porte-container");

//        Generator generator = new Generator();
//        planification = generator.t;

        IntVar[][] position = model.intVarMatrix("position",nbCont,nbStop, -1, maxPos, false);
        IntVar[][] support = model.intVarMatrix("support", nbCont, nbStop, -2, maxPos, false);
        IntVar[][] move = model.intVarMatrix("move", nbCont, nbStop, 0, 2, false);
        IntVar[] restow = model.intVarArray("restow", nbStop, 0, nbCont, false);
        IntVar restowTot = model.intVar("total restow", 0, nbStop * nbCont, false);

        // calcul du restow total
        model.sum(restow, "=", restowTot).post();
        model.arithm(restowTot, "=", 0).post();

        for (int i=0 ; i<nbStop ; i++){
            IntVar I = model.intVar(i);

            // calcul restow(i)
            model.count(2, ArrayUtils.getColumn(move, i), restow[i]).post();

            for (int c=0 ; c<nbCont ; c++){
                // position valide
                model.arithm(position[c][i].abs().div(1000).intVar(),
                        "<", nbBaies).post();
                model.arithm(position[c][i].abs().mod(1000).div(100).intVar(),
                        "<", nbBlocs).post();
                model.arithm(position[c][i].abs().mod(100).div(10).intVar(),
                        "<", nbPiles).post();
                model.arithm(position[c][i].abs().mod(10).intVar(), "<",
                        hMax).post();

                // toutes les positions affectée doivent être différentes pour un même i ou égale à -1
                for (int d = c + 1; d < nbCont; d++) model.ifThen(
                        model.arithm(position[c][i], "!=", -1),
                        model.arithm(position[c][i], "!=", position[d][i])
                );

                // calcul du support
                model.ifThen(
                        model.arithm(position[c][i].mod(10).intVar(), "=", 0),
                        model.arithm(support[c][i], "=", -2)
                );
                model.ifThen(
                        model.arithm(position[c][i].mod(10).intVar(), "=", hMax/2),
                        model.arithm(support[c][i], "=", -1)
                );
                model.ifThen(
                        model.and(
                                model.arithm(position[c][i].mod(10).intVar(), "!=", hMax/2),
                                model.arithm(position[c][i].mod(10).intVar(), "!=", 0)
                        ),
                        model.arithm(support[c][i], "=", position[c][i].sub(1).intVar())
                );


                // condition de pile en fonction du support
                model.or(
                        model.arithm(support[c][i], "=", -1),
                        model.arithm(support[c][i], "=", -2),
                        model.count(support[c][i].intVar(),
                                ArrayUtils.getColumn(position, i),
                                model.intVar(1))
                );

                // tout container doit être transporté entre son départ et sa destination
                model.ifOnlyIf(
                        model.and(model.arithm(I, ">", load(c)),
                                model.arithm(I, "<=", dest(c))),
                        model.arithm(position[c][i], "!=", -1)
                );

                if(i < nbStop - 1) {
                    // move=1 si le container est chargé ou déchargé
                    model.ifOnlyIf(
                            model.and(
                                    model.arithm(position[c][i], "!=", position[c][i + 1]),
                                    model.or(
                                            model.arithm(position[c][i], "=", -1),
                                            model.arithm(position[c][i + 1], "=", -1)
                                    )
                            ),
                            model.arithm(move[c][i], "=", 1)
                    );

                    // move=0 si la position du container n'a pas changé
                    // (on suppose pour l'instant qu'après un restow, on ne remet pas le container à la même position)
                    model.ifOnlyIf(
                            model.arithm(position[c][i], "=", position[c][i + 1]),
                            model.arithm(move[c][i], "=", 0)
                    );

                    //move=2 si le container a changé de position
                    model.ifOnlyIf(
                            model.and(
                                    model.arithm(position[c][i], "!=", position[c][i + 1]),
                                    model.and(
                                            model.arithm(position[c][i], "!=", -1),
                                            model.arithm(position[c][i + 1], "!=", -1)
                                    )
                            ),
                            model.arithm(move[c][i], "=", 2)
                    );
                } else{
                    // pour la dernière étape, move=0 si le container n'est plus dans le navire, et move=1 sinon (déchargement)
                    model.ifThenElse(
                            model.arithm(position[c][i], "=", -1),
                            model.arithm(move[c][i], "=", 0),
                            model.arithm(move[c][i], "=", 1)
                    );
                }
            }
        }
        Solver solver = model.getSolver();
        solver.showStatistics();
        solver.showSolutions();
        Solution solution = solver.findOptimalSolution(restowTot, false);
        System.out.println(Arrays.deepToString(planification));
        writeSolution(generatePosToCont(position, solution), translatePos());
    }
    private static int load(int c) {return planification[c][0];}
    private static int dest(int c) {return planification[c][1];}
    private static void writeSolution(int[][] posToCont, int[] translationPos) throws IOException {
        for (int i=1 ; i<nbStop ; i++) {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./solution-stop" + i + ".txt"));
            int ip = 0;
            for (int nbba = 0; nbba < nbBaies; nbba++) {
                for (int d = 0; d < 2; d++) {
                    for (int j = 0; j < hMax / 2; j++) {
                        for (int nbbl = 0; nbbl < nbBlocs; nbbl++) {
                            for (int nbp = 0; nbp < nbPiles; nbp++) {
                                writer.append(intToString(posToCont[translationPos[ip]][i]));
                                ip++;
                            }
                            writer.append("|");
                        }
                        writer.append("\n");
                    }
                    writer.append("--------------\n");
                }
                writer.append("\n");
            }
            writer.close();
        }
    }
    private static int[][] generatePosToCont(IntVar[][] position, Solution solution){
        int[][] t = new int[maxPos][nbStop];
        for (int p = 0; p < maxPos; p++) {
            for (int i = 0; i < nbStop; i++) {
                t[p][i] = -1;
            }
        }
        for (int c = 0;c < nbCont;c++){
            for (int i = 0; i < nbStop; i++) {
                if(solution.getIntVal(position[c][i]) != -1) {
                    t[solution.getIntVal(position[c][i])][i] = c;
                }else t[c][i] = -1;
            }
        }
        return t;
    }
    private static int[] translatePos(){
        int[] t = new int[nbBaies * nbBlocs * nbPiles * hMax];
        int j = 0;
        for (int nba = 0; nba < nbBaies; nba++) {
            for (int i = hMax - 1; i >= 0; i--) {
                for (int nbl = 0; nbl < nbBlocs; nbl++) {
                    for (int np = 0; np < nbPiles; np++) {
                        t[j] = i + (10 * np) + (100 * nbl) + (1000 * nba);
                        j++;
                    }
                }
            }
        }
        return t;
    }
    private static String intToString(int i){
        if(i>99) return String.valueOf(i);
        else if(i<0) return " " + i;
        else if(i>9) return "+" + i;
        else return " +" + i;
    }
    private static void printSolution(Solution solution, IntVar[][] position, IntVar[][] move, IntVar[] restow, IntVar restowTot){
        if(solution != null) {
            for (int c = 0; c < nbCont; c++) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("position[" + c + "][" + i + "] = " + solution.getIntVal(position[c][i]) + " ");
                }
                System.out.print("\n");
            }
            for (int c = 0; c < nbCont; c++) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("move[" + c + "][" + i + "] = " + solution.getIntVal(move[c][i]) + " ");
                }
                System.out.print("\n");
            }
            for (int i = 0; i < nbStop; i++) {
                System.out.print("restow[" + i + "] = " + solution.getIntVal(restow[i]) + " ");
            }
            System.out.print("\n");
            System.out.println(solution.getIntVal(restowTot));
        }
    }
}
