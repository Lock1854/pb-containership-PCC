package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Container {
    int load, unload;
    IntVar[] positions;
    public Container(int load, int unload, IntVar[] positions){
        this.load = load;
        this.unload = unload;
        this.positions = positions;
    }
}
