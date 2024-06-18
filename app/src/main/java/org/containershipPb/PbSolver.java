package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;

import java.util.Arrays;

public class PbSolver {
    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Navire navire;
        Variables variables;
        Contraintes contraintes;
//        contraintes.postContraints();

//        model.getSolver().showStatistics();
//        Solution sol = model.getSolver().findOptimalSolution(variables.restowTot, false);
//        System.out.println(Arrays.deepToString(navire.planification));
//        printSolution(sol, variables, navire);
    }
}
