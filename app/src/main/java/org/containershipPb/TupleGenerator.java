package org.containershipPb;

import org.chocosolver.solver.constraints.extension.Tuples;

import static org.containershipPb.PbSolver.*;

public class TupleGenerator {

    public TupleGenerator(){
    }

    static boolean show = false;
    public Tuples getContPosEquiv(Container cont, Position pos, int i){
        Tuples t = new Tuples(true);
        for (int p = cont.positions[i].getLB(); p <= cont.positions[i].getUB(); p++) {
            for (int c : data.onboardContsNo(i)) {
                if ((c != cont.number && p != pos.number) || (c == cont.number && p == pos.number)){
                    t.add(c,p);
                }
            }
            for (int q = cont.positions[i].getLB(); q <= cont.positions[i].getUB(); q++) {
                if (q != p) t.add(-q,p);
            }
        }
        if (show) {
            System.out.println(
                    "Position : " + pos.number + "\n"
                            + "Container : " + cont.number + "\n"
                            + t
            );
            show = false;
        }
        return t;
    }

    public Tuples getMovePosTuple(Position pos, Boolean lastStep){
        int star = nbCont + 1;
        Tuples t = new Tuples(true);
        t.setUniversalValue(star);
        if (lastStep) {
            t.add(-pos.number, 0);
            for (int c = 1; c <= nbCont; c++) {
                t.add(c, 1);
            }
        } else {
            t.add(-pos.number, -pos.number, 0, star);
            for (int c = 1; c <= nbCont; c++) {
                t.add(c, -pos.number, 1, star);
                t.add(-pos.number, c, 1, star);
                for (int d = 1; d <= nbCont; d++) {
                    if (c != d){
                        t.add(c,d,2,star);
                    } else{
                        if (pos.support == null) t.add(c,d,0,star);
                        else {
                            t.add(c,d,0,0);
                            t.add(c,d,2,1);
                            t.add(c,d,2,2);
                        }
                    }
                }
            }
        }
        return t;
    }

    public Tuples getMovePanTuple(){
        Tuples t = new Tuples(true);
        t.add(0,0);
        t.add(0,2);
        t.add(1,2);
        t.add(2,2);
        return t;
    }

    public Tuples getStackTuple(Position pos){
        Tuples t = new Tuples(false);
        for (int c = 1; c <= nbCont; c++) {
            t.add(c,-pos.support.number);
        }
        return t;
    }
}
