package org.containershipPb;

import java.util.ArrayList;

public class Navire {
    final int nbPos, nbPosPan, nbBay, nbBloc, nbPileAbove, nbPileUnder, hMaxUnder, hMaxAbove;
    ArrayList<Bay> bayList;
    static int numberPos = 0, numberPan;
    static ArrayList<Position> positions, panneaux;

    public Navire(int nbBay, int nbBloc, int nbPileAbove, int nbPileUnder, int nbPosAbove, int nbPosUnder){
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
        panneaux = new ArrayList<>();
        ArrayList<Bay> baies = new ArrayList<>();
        for (int bay = 0; bay < nbBay; bay++) {
            baies.add(new Bay(nbBloc, nbPileAbove,nbPileUnder, nbPosAbove, nbPosUnder));
        }
        this.bayList = baies;
        positions.addAll(panneaux);
    }

    private int computeNbPos(){
        return nbBay * nbBloc * (nbPileAbove * hMaxAbove + nbPileUnder * hMaxUnder);
    }

    private int computeNbPosPan(){
        return nbPos + nbBay * nbBloc;
    }
}