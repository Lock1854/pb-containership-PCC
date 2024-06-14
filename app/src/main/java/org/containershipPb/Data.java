package org.containershipPb;

import java.util.HashSet;
import java.util.Set;

public class Data {
    final int nbCont, nbStop, nbPos, nbPosPan;
    private final int nbBaies, nbBlocs, nbPiles, hMax;
    private final int[][] planification;
    final Set<Integer> supportless, hold;
    final Panneau[] panList;
    public Data(int nbCont, int nbStop, int nbBaies, int nbBlocs, int nbPiles, int hMax){
        this.nbCont = nbCont;
        this.nbPos = nbBaies * nbBlocs * nbPiles * hMax;
        this.nbPosPan = nbPos + nbBlocs * nbBaies;
        this.nbStop = nbStop;
        this.nbBaies = nbBaies;
        this.nbBlocs = nbBlocs;
        this.nbPiles = nbPiles;
        this.hMax = hMax;
        Generator generator = new Generator(nbCont, nbStop);
        planification = generator.planification;
        supportless = generateSupportless();
        hold = generateHold();
        panList = generatePanList();
    }

    public Panneau pan(int p){
        int a = 1, l = 1, noBay, noBloc;
        while (p >= a * nbPos/nbBaies) a++;
        noBay = a - 1;
        while (p >= (noBay * nbPos/nbBaies) + l * nbPos/(nbBaies * nbBlocs)) l++;
        noBloc = l - 1;
        return panList[noBloc + nbBlocs * noBay];
    }

    public int load(int c){return planification[c][0];}

    public int unload(int c){return planification[c][1];}

    public int nbLoad(int i){
        int n = 0;
        for (int c = 0; c < nbCont; c++) if (load(c) == i) n++;
        return n;
    }

    public int nbUnload(int i){
        int n = 0;
        for (int c = 0; c < nbCont; c++) if (unload(c) == i) n++;
        return n;
    }

    public Integer blocked(int p){
        if (supportless.contains(p) && hold.contains(p)) return null;
        if (supportless.contains(p) && !hold.contains(p)) return pan(p).numero;
        return p - 1;
    }

    private Set<Integer> generateSupportless(){
        int mod = hMax * 2;
        Set<Integer> s = new HashSet<Integer>();
        for (int p = 0; p < nbPos; p++) {
            if (p%mod == 0 || p%mod == hMax) s.add(p);
        }
        return s;
    }

    private Set<Integer> generateHold(){
        int mod = hMax * 2;
        Set<Integer> s = new HashSet<Integer>();
        for (int p = 0; p < nbPos; p++) {
            if (p%mod < hMax) s.add(p);
        }
        return s;
    }

    private Panneau[] generatePanList() {
        Panneau[] t = new Panneau[nbBlocs * nbBaies];
        for (int b = 0; b < nbBlocs * nbBaies; b++) {
            t[b] = new Panneau(nbPos + b);
        }
        return t;
    }
}
