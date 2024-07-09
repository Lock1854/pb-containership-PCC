package org.containershipPb;

import java.util.ArrayList;
import java.util.Random;

import static org.containershipPb.PbSolver.*;

public class Data {
    static ArrayList<Container> containers;
    ArrayList<Type> types;
    ArrayList<ArrayList<Container>> onboardConts;
    int maxTries = 10;
    static Random random = new Random();
    int numberTypes = 4, minTypeLength = 3;

    public Data(){
        onboardConts = new ArrayList<>(nbStop);
        int step;
        if (seed != -1) maxTries = 1;
        do {
            this.types = generateTypes();
            containers = buildConts();
            onboardConts.clear();
            step = 0;
            while (step < nbStop) {
                ArrayList<Container> list = onboardConts(step);
                if (list.size() > ship.nbPos) break;
                onboardConts.add(list);
                step++;
            }
            if (step < nbStop) {
                if (--maxTries <= 0) System.exit(0);
                seed = -1;
            }
        } while (step < nbStop);
    }

    private ArrayList<Container> onboardConts(int i){
        ArrayList<Container> L = new ArrayList<>();
        for (Container cont : containers){
            if (i > cont.load && i <= cont.unload) L.add(cont);
        }
        return L;
    }

    public int[] onboardContsNo(int i, int p){
        ArrayList<Container> L = onboardConts.get(i);
        int[] T = new int[L.size() + 1];
        for (int j = 0; j < L.size(); j++) {
            T[j] = L.get(j).number;
        }
        T[L.size()] = - p;
        return T;
    }

    public int[] onboardContsNo(int i){
        ArrayList<Container> L = onboardConts.get(i);
        int[] T = new int[L.size()];
        for (int j = 0; j < L.size(); j++) {
            T[j] = L.get(j).number;
        }
        return T;
    }

    public int[] Load(int i){
        int[] T = new int[nbLoad(i)];
        int index = 0;
        for (Container cont : containers) {
            if (cont.load == i){
                T[index] = cont.number;
                index++;
                if (index == nbLoad(i)) break;
            }
        }
        return T;
    }

    public int[] Unload(int i){
        int[] T = new int[nbUnload(i)];
        int index = 0;
        for (Container cont : containers) {
            if (cont.unload == i) {
                T[index] = cont.number;
                index++;
                if (index == nbUnload(i)) break;
            }
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
        for (Container cont : containers){
            if (cont.unload == i) n++;
        }
        return n;
    }

    private ArrayList<Container> buildConts(){
        ArrayList<Container> l = new ArrayList<>(nbCont);
        for (int c = 0; c <nbCont; c++) {
            Type type = types.get(random.nextInt(numberTypes));
            Container cont = new Container(type, c+1);
            l.add(cont);
            type.containers.add(cont);
        }
        return l;
    }

    static int seed = 1575406768;
    private ArrayList<Type> generateTypes(){
        ArrayList<Type> l = new ArrayList<>(numberTypes);
        if (seed == -1) seed = Math.abs(random.nextInt());
        System.out.printf("seed = %d\n", seed);
        random.setSeed(seed);
        for (int j = 0; j < numberTypes; j++) {
            l.add(generateValidType());
        }
        return l;
    }

    static int nbTypes = 0;
    private Type generateValidType() {
        int first, second;
        do {
            first = random.nextInt(nbStop);
            second = random.nextInt(nbStop);
        } while (first >= second || second - first < minTypeLength);
        nbTypes++;
        return new Type(first, second, nbTypes-1);
    }
}
