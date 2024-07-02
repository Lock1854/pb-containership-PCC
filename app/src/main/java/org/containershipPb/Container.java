package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Container {
    int load, unload, number;
    public Container(int load, int unload, int number){
        this.load = load;
        this.unload = unload;
        this.number = number;
    }
}
