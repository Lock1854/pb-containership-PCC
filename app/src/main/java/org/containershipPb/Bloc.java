package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

import static org.containershipPb.Ship.*;
import static org.containershipPb.PbSolver.nbStop;

public class Bloc {
    int nbPilesAbove, nbPilesUnder;
    ArrayList<Pile> pileListAbove, pileListUnder;
    Position hatch;
    Bay bay;
    ArrayList<ArrayList<Position>> bottomPosUnder, bottomPosAbove;

    public Bloc(int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder, Bay bay){
        this.nbPilesAbove = nbPileAbove;
        this.nbPilesUnder = nbPilesUnder;
        this.bay = bay;
        this.pileListAbove = new ArrayList<>();
        this.pileListUnder = new ArrayList<>();
        this.hatch = new Position(null, numberPan, true, new IntVar[nbStop], this);
        this.bottomPosUnder = new ArrayList<>();
        for (int l = 0; l < nbPosUnder; l++) {
            bottomPosUnder.add(new ArrayList<>());
        }
        this.bottomPosAbove = new ArrayList<>();
        for (int l = 0; l < nbPosAbove; l++) {
            bottomPosAbove.add(new ArrayList<>());
        }
        numberPan++;
        hatches.add(hatch);
        for (int i = 0; i < nbPilesUnder; i++) {
            this.pileListUnder.add(new Pile(nbPosUnder, this, true));
        }
        for (int i = 0; i < nbPileAbove; i++) {
            this.pileListAbove.add(new Pile(nbPosAbove, this, false));
        }
    }
}
