package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Variables {
    Model model;
    Data data;
    Position[][] position;
    Container[][] container;
    IntVar[][] move;
    public Variables(Model model, Data data){
        this.model = model;
        this.data = data;
        generatePosition();
        generateContainer();
        move = model.intVarMatrix("move", data.nbPosPan, data.nbStop, 0, 2, false);
    }
    private void generatePosition(){
        position = new Position[data.nbCont][data.nbStop];
        for (int i = 0; i < data.nbStop; i++) {
            for (int c = 0; c < data.nbCont; c++) {
                position[c][i] = new Position(model.intVar("position_"+c+"_"+i, 0, data.nbPosPan));
            }
        }
    }
    private void generateContainer(){
        container = new Container[data.nbPosPan][data.nbStop];
        for (int i = 0; i < data.nbStop; i++) {
            for (int p = 0; p < data.nbPosPan; p++) {
                container[p][i] = new Container(model.intVar("container_"+p+"_"+i, 0, data.nbCont));
            }
        }
    }
}
