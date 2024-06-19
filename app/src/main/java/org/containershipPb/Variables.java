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
    Container[] containers;
    public Variables(Model model, Navire navire, int nbStop, int nbCont, int[][] planification){
        this.model = model;
        this.navire = navire;
        this.nbCont = nbCont;
        this.nbStop = nbStop;
        move = model.intVarMatrix("move", navire.nbPosPan, nbStop, 0, 2, false);
        restow = model.intVarArray("restow", nbStop, 0, navire.nbPos);
        restowTot = model.intVar("restowTot", 0, nbCont);
        containers = buildConts(planification);
        initialisePosVar();
        initialiseContVar();
    }

    private void initialisePosVar(){
        for (Bay bay : navire.bayList){
            for (Bloc bloc : bay.blocList){
                for (Pile pile : bloc.pileList){
                    for (Position pos : pile.posList){
                        for (int i = 0; i < pos.containers.length; i++) {
                            // restreindre domaine aux containers transportés
                            pos.containers[i] = model.intVar("container[" + i + "]", 0, nbCont);
                        }
                    }
                }
            }
        }
    }

    private void initialiseContVar(){
        for (Container cont : containers){
            for (int i = 0; i < cont.positions.length; i++) {
                if (i > cont.load && i <= cont.unload) {
                    cont.positions[i] = model.intVar("position[" + i + "]", 0, nbCont);
                } else cont.positions[i] = null;
            }
        }
    }

    private Container[] buildConts(int[][] planification){
        Container[] t = new Container[nbCont];
        for (int c = 0; c <nbCont; c++) {
            t[c] = new Container(planification[c][0], planification[c][1], new IntVar[nbStop], c);
        }
        return t;
    }
}
