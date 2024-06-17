package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;

public class PbSolver {
    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Data data = new Data(8, 4, 2, 2, 4, 2);
        Variables variables = new Variables(model, data);
        Contraintes contraintes = new Contraintes(data, model, variables);
        contraintes.postContraints();

        model.getSolver().showStatistics();
        Solution sol = model.getSolver().findOptimalSolution(variables.restowTot, false);
        System.out.println(Arrays.deepToString(data.planification));
        printSolution(sol, variables, data);
    }

    private static void printSolution(Solution solution, Variables vars, Data data){
        if(solution != null) {
                for (int c = 0; c < data.nbCont; c++) {
                    for (int i = 0; i < data.nbStop; i++) {
                        if (vars.position[c][i] != null) {
                            System.out.print("pos(" + c + "," + i + ") = " + solution.getIntVal(vars.position[c][i].intVar) + " ");
                        } else {
                            System.out.print("pos(" + c + "," + i + ") = null ");
                        }
                    }
                    System.out.print("\n");
                }
                for (int p = 0; p < data.nbPos; p++) {
                    for (int i = 0; i < data.nbStop; i++) {
                        if (solution.getIntVal(vars.container[p][i].intVar) != -1) {
                            System.out.print("cont(" + p + "," + i + ") = " + solution.getIntVal(vars.container[p][i].intVar) + " ");
                        } else {
                            System.out.print("cont(" + p + "," + i + ") = null ");
                        }
                    }
                    System.out.print("\n");
                }
            for (int c = 0; c < data.nbPos; c++) {
                for (int i = 0; i < data.nbStop; i++) {
                    System.out.print("move[" + c + "][" + i + "] = " + solution.getIntVal(vars.move[c][i]) + " ");
                }
                System.out.print("\n");
            }
            for (int i = 0; i < data.nbStop; i++) {
                System.out.print("restow[" + i + "] = " + solution.getIntVal(vars.restow[i]) + " ");
            }
        } else System.out.println("No solution found");
    }
}
