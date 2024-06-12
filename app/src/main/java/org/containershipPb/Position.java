package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Position {
    Data data;
    Position support;
    IntVar intVar;
    public Position(IntVar intVar, Data data){
        this.data = data;
        this.intVar = intVar;
    }
//    private Position getSupport(){
//        if (altitude != null){
//            if (altitude == 0) {
//                if (noPile % 2 == 0) return null;
//                else return new PosPanneau(noBay, noBloc, data);
//            } else return new Position(noBay, noBloc, noPile, altitude - 1, data);
//        }
//        return null;
//    }
//    private void getCoord(){
//        int a = 0, l = 0;
//        while (numero < a * data.nbPos/data.nbBaies) a++;
//        noBay = a - 1;
//        while (numero < (a * data.nbPos/data.nbBaies) + l)
//    }
}
