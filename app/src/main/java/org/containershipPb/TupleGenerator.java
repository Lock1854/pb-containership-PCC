package org.containershipPb;

import org.chocosolver.solver.constraints.extension.Tuples;

import static org.containershipPb.Navire.positions;
import static org.containershipPb.PbSolver.nbCont;

public class TupleGenerator {
    Boolean show = false;
    Data data;

    public TupleGenerator(Data data){
        this.data = data;
    }

    public Tuples getMovePos(Boolean lastStep, Boolean isSupported){
        int star = -2;
        Tuples t = new Tuples(true);
        t.setUniversalValue(star);
        if (lastStep) {
            for (int c = -1; c < nbCont; c++) {
                for (int m = 0; m < 3; m++) {
                    if ((c == -1 && m == 0) || (c != -1 && m == 1)){
                        t.add(c,m);
                    }
                }
            }
        } else {
            for (int c = -1; c < nbCont; c++) {
                for (int d = -1; d < nbCont; d++) {
                    if (c != d){
                        if (c == -1 || d == -1) t.add(c,d,1,star);
                        else t.add(c,d,2,star);
                    } else{
                        if (c == -1) t.add(c,d,0,star);
                        else {
                            if (!isSupported) t.add(c,d,0,star);
                            else {
                                t.add(c,d,0,0);
                                t.add(c,d,2,1);
                                t.add(c,d,2,2);
                            }
                        }
                    }
                }
            }
        }
        return t;
    }

    public Tuples getMovePan(){
        Tuples t = new Tuples(true);
        t.add(0,0);
        t.add(0,2);
        t.add(1,2);
        t.add(2,2);
        return t;
    }

    public Tuples getPile(){
        Tuples t = new Tuples(false);
        for (int c = 0; c < positions.size(); c++) {
            t.add(-1,c);
        }
        return t;
    }
}
