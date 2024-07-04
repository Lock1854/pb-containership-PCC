package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.Variable;


public class PbSolver {
    static int nbCont = 4, nbStop = 10, nbBay = 1, nbBloc = 1, nbPileAbove = 2, nbPileUnder = 0, nbPosAbove = 3, nbPosUnder = 2;
    static int nbVar = 0;
    int nbVarSup;

    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Navire navire = new Navire(nbBay, nbBloc, nbPileAbove, nbPileUnder, nbPosAbove, nbPosUnder);
        Data data = new Data();
        CSP csp = new CSP(model, navire, data, true, false);
        csp.solve("print");
        if (false) for (Variable vars : model.getVars()){
            System.out.println(vars);
        }
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
