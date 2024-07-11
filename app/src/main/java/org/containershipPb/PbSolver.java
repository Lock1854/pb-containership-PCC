package org.containershipPb;

import org.chocosolver.solver.Model;


public class PbSolver {
    static int nbCont = 33, nbStop = 11, nbBay = 2, nbBloc = 2, nbPileAbove = 2, nbPileUnder = 2, nbPosAbove = 2, nbPosUnder = 2;
    static int nbVar = 0;
    int nbVarSup;
    static Ship ship;
    static Data data;
    static Model model;

    public static void main(String[] args){
        model = new Model("Chargement navire porte-container");
        ship = new Ship(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        data = new Data();
        for (Type type : data.types) {
            System.out.println(type);
        }
        CSP csp = new CSP(true, true);
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
