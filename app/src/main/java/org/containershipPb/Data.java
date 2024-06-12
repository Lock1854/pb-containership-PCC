package org.containershipPb;

public class Data {
    int nbCont, nbStop, nbBaies, nbBlocs, nbPiles, hMax, nbPos;
    public Data(int nbCont, int nbStop, int nbBaies, int nbBlocs, int nbPiles, int hMax){
        this.nbCont = nbCont;
        this.nbStop = nbStop;
        this.nbBaies = nbBaies;
        this.nbBlocs = nbBlocs;
        this.nbPiles = nbPiles;
        this.hMax = hMax;
        this.nbPos = nbBaies * nbBlocs * nbPiles * hMax;
    }
}
