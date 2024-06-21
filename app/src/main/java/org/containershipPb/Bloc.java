package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Navire.*;

public class Bloc {
    int nbPilesAbove, nbPilesUnder;
    ArrayList<Pile> pileListAbove, pileListUnder;
    Position panneau;
    Bay bay;

    public Bloc(ArrayList<Pile> pileListAbove, ArrayList<Pile> pileListUnder, Position panneau, Bay bay){
        this.pileListAbove = pileListAbove;
        this.pileListUnder = pileListUnder;
        this.panneau = panneau;
        this.bay = bay;
        nbPilesAbove = pileListAbove.size();
        nbPilesUnder = pileListUnder.size();
    }

    public Bloc(int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder, Bay bay){
        this.nbPilesAbove = nbPileAbove;
        this.nbPilesUnder = nbPilesUnder;
        this.bay = bay;
        this.pileListAbove = new ArrayList<>();
        this.pileListUnder = new ArrayList<>();
        for (int i = 0; i < nbPilesUnder; i++) {
            this.pileListUnder.add(new Pile(nbPosUnder, this));
            numberPos++;
        }
        for (int i = 0; i < nbPileAbove; i++) {
            this.pileListAbove.add(new Pile(nbPosAbove, this));
            numberPos++;
        }
        this.panneau = new Position(null, numberPan, true, null, this);
        numberPan++;
        panneaux.add(panneau);
    }
}
