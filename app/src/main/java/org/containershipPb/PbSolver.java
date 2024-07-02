package org.containershipPb;

import org.chocosolver.solver.Model;


public class PbSolver {
    static int nbCont = 10, nbStop = 4, nbBay = 2, nbBloc = 2, nbPileAbove = 2, nbPileUnder = 0, nbPosAbove = 2, nbPosUnder = 2;
    static int nbVar = 0;
    int nbVarSup;

    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, navire, data, true, false);
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
