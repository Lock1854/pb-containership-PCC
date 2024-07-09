package org.containershipPb;

import java.util.ArrayList;

public class Ship {
    final int nbPos, nbPosPan, nbBay, nbBloc, nbPileAbove, nbPileUnder, hMaxUnder, hMaxAbove;
    ArrayList<Bay> bayList;
    static int numberPos = 0, numberPan;
    static ArrayList<Position> positions, hatches;
    static ArrayList<ArrayList<Position>> symPosStack, symPosBloc, symPosBay;
    static int posInBloc, posInBay;

    public Ship(int nbBay, int nbBloc, int nbPileAbove, int nbPileUnder, int nbPosAbove, int nbPosUnder){
        this.nbBay = nbBay;
        this.nbBloc = nbBloc;
        this.nbPileAbove = nbPileAbove;
        this.nbPileUnder = nbPileUnder;
        this.hMaxAbove = nbPosAbove;
        this.hMaxUnder = nbPosUnder;
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
        numberPan = nbPos;
        positions = new ArrayList<>();
        hatches = new ArrayList<>();
        symPosStack = new ArrayList<>();
        if (nbBloc > 1) {
            symPosBloc = new ArrayList<>();
            for (int p = 0; p < (nbPosAbove * nbPileAbove + nbPosUnder * nbPileUnder); p++) {
                symPosBloc.add(new ArrayList<>());
            }
        }
        if (nbBay > 1) {
            symPosBay = new ArrayList<>();
            for (int p = 0; p < (nbPosAbove * nbPileAbove + nbPosUnder * nbPileUnder) * nbBloc; p++) {
                symPosBay.add(new ArrayList<>());
            }
        }
        ArrayList<Bay> baies = new ArrayList<>();
        for (int bay = 0; bay < nbBay; bay++) {
            baies.add(new Bay(nbBloc, nbPileAbove,nbPileUnder, nbPosAbove, nbPosUnder));
        }
        positions.addAll(hatches);
        this.bayList = baies;
    }

    private int computeNbPos(){
        return nbBay * nbBloc * (nbPileAbove * hMaxAbove + nbPileUnder * hMaxUnder);
    }

    private int computeNbPosPan(){
        return nbPos + nbBay * nbBloc;
    }
}