package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Position {
    Position support;
    IntVar intVar;

    public Position(IntVar intVar){
        this.intVar = intVar;
    }
}
