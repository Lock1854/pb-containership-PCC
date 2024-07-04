package org.containershipPb;

import org.chocosolver.solver.Model;


public class PbSolver {
    static int nbCont = 8, nbStop = 4, nbBay = 1, nbBloc = 2, nbPileAbove = 2, nbPileUnder = 0, nbPosAbove = 2, nbPosUnder = 0;
    static int nbVar = 0;
    int nbVarSup;

    public static void main(String[] args){
        Model model = new Model("Chargement navire porte-container");
        Ship ship = new Ship(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, ship, data, false, false);
        csp.solve("print");
    }

    public PbSolver(){
        nbVar = 0;
        Ship.numberPos = 0;
        Model model = new Model("Chargement navire porte-container");
        Ship ship = new Ship(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, ship, data, true, false);
        csp.postContraints();
        nbVarSup = csp.model.getNbVars() - nbVar;
    }
}
