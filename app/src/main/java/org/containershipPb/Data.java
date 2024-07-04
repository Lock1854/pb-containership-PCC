package org.containershipPb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.containershipPb.PbSolver.nbCont;
import static org.containershipPb.PbSolver.nbStop;

public class Data {
    static Container[] containers;
    int[][] planification;
    ArrayList<Container>[] onboardConts;

    public Data(){
        this.planification = generatePlannification(3);
        System.out.println(Arrays.deepToString(this.planification));
        containers = buildConts();
        onboardConts = new ArrayList[nbStop];
        for (int i = 0; i < nbStop; i++) {
            onboardConts[i] = onboardConts(i);
        }
    }

    private ArrayList<Container> onboardConts(int i){
        ArrayList<Container> L = new ArrayList<>();
        for (Container cont : containers){
            if (i > cont.load && i <= cont.unload) L.add(cont);
        }
        return L;
    }

    public int[] onboardContsNo(int i, Position pos){
        ArrayList<Container> L = onboardConts[i];
        int[] T = new int[L.size() + 1];
        for (int j = 0; j < L.size(); j++) {
            T[j] = L.get(j).number;
        }
        T[L.size()] = - pos.number;
        return T;
    }

    public int[] onboardContsNo(int i){
        ArrayList<Container> L = onboardConts[i];
        int[] T = new int[L.size()];
        for (int j = 0; j < L.size(); j++) {
            T[j] = L.get(j).number;
        }
        return T;
    }

    public int[] Load(int i){
        int[] T = new int[nbLoad(i)];
        for (int c = 0; c < nbCont; c++) {
            if (containers[i].load == i) T[c] = containers[i].number;
        }
        return T;
    }

    public int[] Unload(int i){
        int[] T = new int[nbUnload(i)];
        for (int c = 0; c < nbCont; c++) {
            if (containers[i].unload == i) T[c] = containers[i].number;
        }
        return T;
    }

    public int nbLoad(int i){
        int n = 0;
        for (Container cont : containers){
            if (cont.load == i) n++;
        }
        return n;
    }

    public int nbUnload(int i){
        int n = 0;
        for (Container cont : onboardConts[i]){
            if (cont.unload == i) n++;
        }
        return n;
    }

    private Container[] buildConts(){
        Container[] t = new Container[nbCont];
        for (int c = 0; c <nbCont; c++) {
            t[c] = new Container(planification[c][0], planification[c][1], c+1);
        }
        return t;
    }

    private int[][] generatePlannification(int numberTypes){
        int[][] t = new int[nbCont][2];
        Random random = new Random();
        int compt = 0;
        int[] pair = null;
        for (int c = 0; c < nbCont; c++) {
            if (compt == 0 && c != nbCont - 1) pair = generateValidPair(random);
            t[c] = pair;
            if (compt == nbCont / numberTypes - 1) compt = 0;
            else compt++;
        }
        return t;
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
