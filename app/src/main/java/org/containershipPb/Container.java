package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Container {
    int load, unload, number;
    IntVar[] positions;
    public Container(int load, int unload, IntVar[] positions, int number){
        this.load = load;
        this.unload = unload;
        this.positions = positions;
        this.number = number;
    }
}
