package org.containershipPb;

import org.checkerframework.checker.units.qual.C;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;
import java.util.Arrays;

public class PbSolver {
    static int nbCont = 10, nbStop = 4;
    public static void main(String[] args) {
        Generator generator = new Generator(nbCont, nbStop);
        int[][] planification = generator.planification;
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(2, 2, 4, 2, nbStop);
        Variables variables = new Variables(model, navire, nbStop, nbCont, planification);
        Contraintes contraintes;
//        contraintes.postContraints();

        model.getSolver().showStatistics();
        model.getSolver().showSolutions();
        model.getSolver().findOptimalSolution(variables.restowTot, false);
    }
}
