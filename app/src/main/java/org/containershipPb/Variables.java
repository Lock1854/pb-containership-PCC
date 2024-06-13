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
        position = new Position[data.nbCont][data.nbStop];
        container = new Container[data.nbPos][data.nbStop];
        move = model.intVarMatrix("move", data.nbPos, data.nbStop, 0, 2, false);
    }
}
