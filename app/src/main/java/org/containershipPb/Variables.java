package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Variables {
    Model model;
    Navire navire;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;
    int nbStop, nbCont;
    public Variables(Model model, Navire navire, int nbStop, int nbCont){
        this.model = model;
        this.navire = navire;
        this.nbCont = nbCont;
        this.nbStop = nbStop;
        move = model.intVarMatrix("move", navire.nbPosPan, nbStop, 0, 2, false);
        restow = model.intVarArray("restow", nbStop, 0, navire.nbPos);
        restowTot = model.intVar("restowTot", 0, nbCont);
    }
}
