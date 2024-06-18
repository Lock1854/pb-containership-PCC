package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Variables {
//    Model model;
//    Navire navire;
//    Position[][] position;
//    Container[][] container;
//    IntVar[][] move;
//    IntVar[] restow;
//    IntVar restowTot;
//    public Variables(Model model, Navire navire){
//        this.model = model;
//        this.navire = navire;
//        generatePosition();
//        generateContainer();
//        move = model.intVarMatrix("move", navire.nbPosPan, navire.nbStop, 0, 2, false);
//        restow = model.intVarArray("restow", navire.nbStop, 0, navire.nbPos);
//        restowTot = model.intVar("restowTot", 0, navire.nbCont);
//    }
//    private void generatePosition(){
//        position = new Position[navire.nbCont][navire.nbStop];
//        for (int i = 0; i < navire.nbStop; i++) {
//            for (int c = 0; c < navire.nbCont; c++) {
//                if (navire.load(c) < i && navire.unload(c) >= i) {
//                    position[c][i] = new Position(model.intVar("position(" + c + ", " + i + ")", 0, navire.nbPos - 1));
//                }
//            }
//        }
//    }
//    private void generateContainer(){
//        container = new Container[navire.nbPos][navire.nbStop];
//        for (int i = 0; i < navire.nbStop; i++) {
//            for (int p = 0; p < navire.nbPos; p++) {
//                container[p][i] = new Container(model.intVar("container(" + p + ", " + i + ")", -1, navire.nbCont));
//            }
//        }
//    }
}
