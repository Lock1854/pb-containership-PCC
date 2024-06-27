package org.containershipPb;

import org.chocosolver.solver.constraints.extension.Tuples;

import static org.containershipPb.Navire.positions;
import static org.containershipPb.PbSolver.nbCont;

public class TupleGenerator {
    static int compt = 0;

    static public Tuples getContPosEquiv(Position pos, Container cont){
        Tuples t = new Tuples(true);
        for (int p = 0; p < positions.size(); p++) {
            for (int c = -1; c < nbCont; c++) {
                if (c != cont.number && p != pos.number){
                    t.add(c,p);
                }
            }
        }
        t.add(cont.number, pos.number);
        if (compt == 0) {
            System.out.println(
                    "Position : " + pos.number + "\n"
                            + "Container : " + cont.number + "\n"
                            + t
            );
            compt++;
        }
        return t;
    }
}
