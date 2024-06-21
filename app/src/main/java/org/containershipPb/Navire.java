package org.containershipPb;

import java.util.ArrayList;

public class Navire {
    final int nbPos, nbPosPan, nbBay, nbBloc, nbPile, hMax;
    ArrayList<Bay> bayList;
    static int numberPos = 0, numberPan;
    static ArrayList<Position> positions, panneaux;

    public Navire(int nbBay, int nbBloc, int nbPileAbove, int nbPileUnder, int nbPosAbove, int nbPosUnder, int nbStop){
        positions = new ArrayList<>();
        panneaux = new ArrayList<>();
        ArrayList<Bay> baies = new ArrayList<>();
        for (int bay = 0; bay < nbBay; bay++) {
            baies.add(new Bay(nbBloc, nbPileAbove,nbPileUnder, nbPosAbove, nbPosUnder));
            numberPos++;
        }
        this.bayList = baies;
        this.nbBay = nbBay;
        this.nbBloc = nbBloc;
        this.nbPile = nbPosAbove + nbPileUnder;
        this.hMax = nbPosAbove + nbPosUnder;
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
        positions.addAll(panneaux);
    }

    private int computeNbPos(){
        return nbBay * nbBloc * nbPile * hMax;
    }

    private int computeNbPosPan(){
        return nbPos + nbBay * nbBloc;
    }
}