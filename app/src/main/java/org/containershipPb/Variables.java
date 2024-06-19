package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Variables {
    Model model;
    Data data;
    Position[][] position;
    Container[][] container;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;

    public Variables(Model model, Data data){
        this.model = model;
        this.data = data;
        generatePosition();
        generateContainer();
        move = model.intVarMatrix("move", data.nbPosPan, data.nbStop, 0, 2, false);
        restow = model.intVarArray("restow", data.nbStop, 0, data.nbPos);
        restowTot = model.intVar("restowTot", 0, data.nbCont);
    }

    private void generatePosition(){
        position = new Position[data.nbCont][data.nbStop];
        for (int i = 0; i < data.nbStop; i++) {
            for (int c = 0; c < data.nbCont; c++) {
                if (data.load(c) < i && data.unload(c) >= i) {
                    position[c][i] = new Position(model.intVar("position(" + c + ", " + i + ")", 0, data.nbPos - 1));
                }
            }
        }
    }

    private void generateContainer(){
        container = new Container[data.nbPos][data.nbStop];
        for (int i = 0; i < data.nbStop; i++) {
            for (int p = 0; p < data.nbPos; p++) {
                container[p][i] = new Container(model.intVar("container(" + p + ", " + i + ")", -1, data.nbCont - 1));
            }
        }
    }
}
