package org.containershipPb;

import org.chocosolver.solver.Model;

public class PbSolver {
    static int nbCont = 10, nbStop = 4, nbBay = 2, nbBloc = 2, nbPileAbove = 2, nbPileUnder = 2, nbPosAbove = 2, nbPosUnder = 2;

    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder, nbStop);
        Data data = new Data();
        CSP csp = new CSP(model, navire, data);
        csp.solve(false);
    }
}
