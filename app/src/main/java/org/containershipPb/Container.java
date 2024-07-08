package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import static org.containershipPb.PbSolver.nbStop;

public class Container {
    int load, unload, number;
    Type type;
    IntVar[] positions;
    public Container(Type type, int number){
        this.load = type.load;
        this.unload = type.unload;
        this.type = type;
        this.number = number;
        positions = new IntVar[nbStop];
    }

    @Override
    public String toString() {
        return load + "\t" + unload + "\t" + type.num + "\t" + number;
    }
}
