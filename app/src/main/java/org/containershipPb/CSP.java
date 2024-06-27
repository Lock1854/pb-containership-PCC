package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import static org.containershipPb.Data.containers;
import static org.containershipPb.Navire.positions;
import static org.containershipPb.PbSolver.*;

public class CSP {
    Model model;
    Navire navire;
    Data data;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;
    Boolean restowAllowed, table;

    public CSP(Model model, Navire navire, Data data, Boolean restowAllowed, Boolean table){
        this.model = model;
        this.navire = navire;
        this.data = data;
        this.restowAllowed = restowAllowed;
        this.table = table;
        move = model.intVarMatrix("move", navire.nbPosPan, nbStop, 0, 2, false);
        nbVar += navire.nbPosPan * nbStop;
        if (restowAllowed) {
            restow = model.intVarArray("restow", nbStop, 0, navire.nbPosPan);
            restowTot = model.intVar("restowTot", 0, nbStop * navire.nbPosPan);
            nbVar += nbStop + 1;
        }
        initialisePosVar();
        initialiseContVar();
    }

    public void solve(String useSolution) {
        postContraints();
        model.getSolver().showStatistics();
        Solver solver = model.getSolver();
        if (useSolution.equals("show")) solver.showSolutions();
        Solution solution = restowAllowed? model.getSolver().findOptimalSolution(restowTot, false)
                : solver.findSolution();
        if (useSolution.equals("print")) printSolution(solution);
    }

    public void postContraints(){
        if (restowAllowed) restowTot();
        for (int i = 0; i < nbStop; i++) {
            if (restowAllowed) restow(i);
            for (Position pos : positions) {
                pile(pos, i);
                movePos(pos, i);
                for (Container cont : data.transportedConts[i]) {
                    positionContainerEquiv(pos, cont, i);
                    if (!restowAllowed) noRestow(pos, cont, i);
                }
            }
        }
    }

    private void positionContainerEquiv(Position pos, Container cont, int i){
        if (cont.positions[i] != null) {
            if (table) {
                model.table(pos.containers[i], cont.positions[i], TupleGenerator.getContPosEquiv(pos, cont), "FC").post();
            } else {
                model.ifOnlyIf(
                        model.arithm(cont.positions[i], "=", pos.number),
                        model.arithm(pos.containers[i], "=", cont.number)
                );
            }
        } else model.arithm(pos.containers[i], "!=", cont.number).post();
    }

    private void pile(Position pos, int i){
        if (pos.support() != null && !pos.support().isPanneau) {
            model.ifThen(
                    model.arithm(pos.containers[i], "!=", -1),
                    model.arithm(pos.support().containers[i], "!=", -1)
            );
        }
    }

    private void movePos(Position pos, int i){
            if (i == nbStop - 1) {
                // cas de la dernière étape
                model.ifThenElse(
                        model.arithm(pos.containers[i], "=", -1),
                        model.arithm(move[pos.number][i], "=", 0),
                        model.arithm(move[pos.number][i], "=", 1)
                );
            } else {
                // cas cont(p,i) != cont(p,i+1) et l'un des deux nul
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                                model.or(
                                        model.arithm(pos.containers[i], "=", -1),
                                        model.arithm(pos.containers[i + 1], "=", -1)
                                )
                        ),
                        model.arithm(move[pos.number][i], "=", 1)
                );
                // cas cont(p,i) != cont(p,i+1) et aucun des deux nuls
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                                model.arithm(pos.containers[i], "!=", -1),
                                model.arithm(pos.containers[i + 1], "!=", -1)
                        ),
                        model.arithm(move[pos.number][i], "=", 2)
                );
                // cas cont(p,i) = cont(p,i+1) = null
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                model.arithm(pos.containers[i], "=", -1)
                        ),
                        model.arithm(move[pos.number][i], "=", 0)
                );
                // cas cont(p,i) = cont(p,i+1) != null
                if (pos.support() == null){
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", -1)
                            ),
                            model.arithm(move[pos.number][i], "=", 0)
                    );
                } else {
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", -1),
                                    model.arithm(move[pos.support().number][i], "=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 0)
                    );
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", -1),
                                    model.arithm(move[pos.support().number][i], "!=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 2)
                    );
                }
            }
        // propagation bloquage
        if (pos.pile.bloc.pileListUnder.contains(pos.pile)){
            model.ifThenElse(
                    model.arithm(move[pos.number][i], "!=", 0),
                    model.arithm(move[pos.pile.bloc.panneau.number][i], "=", 2),
                    model.arithm(move[pos.pile.bloc.panneau.number][i], "=", 0)
            );
        }
    }

    private void restow(int i){
        model.sum(ArrayUtils.getColumn(move, i),
                "=",
                restow[i].add(data.nbUnload(i)).add(data.nbLoad(i)).intVar()
        ).post();
    }

    private void restowTot(){
        model.sum(restow, "=", restowTot).post();
    }

    private void noRestow(Position pos, Container cont, int i){

        if (i < nbStop - 1 && cont.positions[i] != null && cont.positions[i+1] != null) {
            model.ifThen(
                    model.arithm(cont.positions[i], "=", pos.number),
                    model.and(
                            model.arithm(cont.positions[i], "=", cont.positions[i + 1]),
                            model.arithm(move[pos.number][i], "=", 0)
                    )
            );
        }
    }

    // probably unecessary due to positionContainerEquiv, but maybe helpful ?
    private void differentPositions(int i){
        IntVar[] vars = model.intVarArray(nbCont, 0, navire.nbPos);
        int compteur = 0;
        for (Container cont : data.transportedConts[i]) {
            vars[compteur] = cont.positions[i];
            compteur++;
        }
        model.allDifferent(vars).post();
    }

    private void initialisePosVar(){
        for (Position pos : positions){
            if (!pos.isPanneau) {
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("container[" + pos.number + "][" + i + "]", data.transportedContsNo(i));
                    nbVar++;
                }
            }
        }
    }

    private void initialiseContVar(){
        for (Container cont : containers){
            for (int i = 0; i < nbStop; i++) {
                if (i > cont.load && i <= cont.unload) {
                    cont.positions[i] = model.intVar("position[" + cont.number + "][" + i + "]", 0, nbCont);
                    nbVar++;
                } else cont.positions[i] = null;
            }
        }
    }

    private void printSolution(Solution solution){
        if(solution == null) System.out.println("No solution found");
        else {
            for (Container cont : containers) {
                for (int i = 0; i < nbStop; i++) {
                    if (cont.positions[i] != null) {
                        System.out.print("pos(" + cont.number + "," + i + ") = " + solution.getIntVal(cont.positions[i]) + " ; ");
                    } else {
                        System.out.print("pos(" + cont.number + "," + i + ") = null ; ");
                    }
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            for (int p = 0; p < navire.nbPos; p++)  {
                for (int i = 0; i < nbStop; i++) {
                    if (solution.getIntVal(positions.get(p).containers[i]) != -1) {
                        System.out.print("cont(" + positions.get(p).number + "," + i + ") = " + solution.getIntVal(positions.get(p).containers[i]) + " ; ");
                    } else {
                        System.out.print("cont(" + positions.get(p).number + "," + i + ") = null ; ");
                    }
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            for (int c = 0; c < navire.nbPosPan; c++) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("move[" + c + "][" + i + "] = " + solution.getIntVal(move[c][i]) + " ; ");
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            if (restowAllowed) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("restow[" + i + "] = " + solution.getIntVal(restow[i]) + " ; ");
                }
            }
        }
    }
}
