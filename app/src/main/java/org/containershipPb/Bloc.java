package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

import static org.containershipPb.Navire.*;
import static org.containershipPb.PbSolver.nbStop;

public class Bloc {
    int nbPilesAbove, nbPilesUnder;
    ArrayList<Pile> pileListAbove, pileListUnder;
    Position panneau;
    Bay bay;

    public Bloc(int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder, Bay bay){
        this.nbPilesAbove = nbPileAbove;
        this.nbPilesUnder = nbPilesUnder;
        this.bay = bay;
        this.pileListAbove = new ArrayList<>();
        this.pileListUnder = new ArrayList<>();
        this.panneau = new Position(null, numberPan, true, new IntVar[nbStop], this);
        numberPan++;
        panneaux.add(panneau);
        for (int i = 0; i < nbPilesUnder; i++) {
            this.pileListUnder.add(new Pile(nbPosUnder, this));
        }
        for (int i = 0; i < nbPileAbove; i++) {
            this.pileListAbove.add(new Pile(nbPosAbove, this));
        }
    }
}
