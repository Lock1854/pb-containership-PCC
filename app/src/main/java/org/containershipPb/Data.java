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
    int numberTypes = 9, minTypeLength = 3;
    Boolean generateAtRandom = false;
    static int seed = 1512066322;

    public Data(){
        containers = new ArrayList<>();
        onboardConts = new ArrayList<>(nbStop);
        if (seed != -1) maxTries = 1;
        boolean finish;
        do {
            finish = true;
            if (seed == -1) seed = Math.abs(random.nextInt());
            random.setSeed(seed);
            this.types = generateAtRandom? generateTypesRandom() : generateTypesManually();
            buildConts();
            onboardConts.clear();
            for (Type type : types){
                if (type.containers.isEmpty()){
                    finish = false;
                    break;
                }
            }
            if (finish) {
                for (int i = 0; i < nbStop; i++) {
                    ArrayList<Container> list = onboardConts(i);
                    if (list.size() > ship.nbPos) {
                        finish = false;
                        break;
                    }
                    onboardConts.add(list);
                }
            }
            if (!finish) {
                if (--maxTries <= 0) System.exit(0);
                seed = -1;
            }
        } while (!finish);
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

    private void buildConts(){
        System.out.printf("seed = %d\n", seed);
        for (int c = 0; c <nbCont; c++) {
            Type type = types.get(random.nextInt(numberTypes));
            Container cont = new Container(type, c+1);
            containers.add(cont);
            type.containers.add(cont);
        }
    }

    private ArrayList<Type> generateTypesRandom(){
        ArrayList<Type> l = new ArrayList<>(numberTypes);
        for (int j = 0; j < numberTypes; j++) {
            l.add(generateValidType());
        }
        return l;
    }

    private ArrayList<Type> generateTypesManually(){
        ArrayList<Type> l = new ArrayList<>(numberTypes);
        l.add(new Type(0,3,0));
        l.add(new Type(1,4,1));
        l.add(new Type(1,2,2));
        l.add(new Type(2,8,3));
        l.add(new Type(3,7,4));
        l.add(new Type(4,6,5));
        l.add(new Type(6,10,6));
        l.add(new Type(7,9,7));
        l.add(new Type(8,10,8));
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
