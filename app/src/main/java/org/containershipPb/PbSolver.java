package org.containershipPb;

import org.chocosolver.solver.Model;

import java.io.IOException;


public class PbSolver {
    static int nbCont = 4, nbStop = 4, nbBay = 1, nbBloc = 1, nbPileAbove = 2, nbPileUnder = 0, nbPosAbove = 3, nbPosUnder = 0;
    static int nbVar = 0;
    int nbVarSup;

    public static void main(String[] args) throws IOException {
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
