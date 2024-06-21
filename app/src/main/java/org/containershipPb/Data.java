package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.Random;

import static org.containershipPb.PbSolver.nbCont;
import static org.containershipPb.PbSolver.nbStop;

public class Data {
    static Container[] containers;
    int[][] planification;

    public Data(){
        this.planification = generatePlanification();
        containers = buildConts();
    }

    public int[] transportedContsNo(int i){
        ArrayList<Integer> L = new ArrayList<>();
        L.add(-1);
        for (Container cont : containers){
            if (i > cont.load && i <= cont.unload) L.add(cont.number);
        }
        int[] T = new int[L.size()];
        for (int j = 0; j < L.size(); j++) {
            T[j] = L.get(j);
        }
        return T;
    }

    private Container[] buildConts(){
        Container[] t = new Container[nbCont];
        for (int c = 0; c <nbCont; c++) {
            t[c] = new Container(planification[c][0], planification[c][1], new IntVar[nbStop], c);
        }
        return t;
    }

    private int[][] generatePlanification() {
        int[][] planification = new int[nbCont][2];
        Random random = new Random();

        for (int c = 0; c < nbCont; c++) {
            int[] pair = generateValidPair(random);
            planification[c] = pair;
        }
        return planification;
    }

    private int[] generateValidPair(Random random) {
        int first, second;
        do {
            first = random.nextInt(nbStop);
            second = random.nextInt(nbStop);
        } while (first >= second);
        return new int[]{first, second};
    }
}
