package org.containershipPb;

import org.chocosolver.solver.Model;


public class PbSolver {
    static int nbCont = 10, nbStop = 8, nbBay = 1, nbBloc = 1, nbPileAbove = 2, nbPileUnder = 0, nbPosAbove = 5, nbPosUnder = 0;
    static int nbVar = 0;
    int nbVarSup;
    static Ship ship;
    static Data data;
    static Model model;

    public static void main(String[] args){
        model = new Model("Chargement navire porte-container");
        ship = new Ship(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        data = new Data();
        CSP csp = new CSP(false, true);
        csp.solve("print");
    }

    public PbSolver(){
        nbVar = 0;
        Ship.numberPos = 0;
        CSP csp = new CSP(true, false);
        csp.postContraints();
        nbVarSup = model.getNbVars() - nbVar;
    }
}
