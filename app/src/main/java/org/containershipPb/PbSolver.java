package org.containershipPb;

import org.chocosolver.solver.Model;


public class PbSolver {
    static int nbCont = 5, nbStop = 4, nbBay = 2, nbBloc = 1, nbPileAbove = 1, nbPileUnder = 1, nbPosAbove = 2, nbPosUnder = 2;
    static int nbVar = 0;
    int nbVarSup;

    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, navire, data, true, true);
        csp.solve("print");
    }

    public PbSolver(){
        nbVar = 0;
        Navire.numberPos = 0;
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, navire, data, true, false);
        csp.postContraints();
        nbVarSup = csp.model.getNbVars() - nbVar;
    }
}
