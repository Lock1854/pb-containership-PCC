package org.containershipPb;

import java.util.ArrayList;

public class Bloc {
    int nbPiles;
    ArrayList<Pile> pileList;
    Position panneau;
    public Bloc(ArrayList<Pile> pileList, Position panneau){
        this.pileList = pileList;
        this.panneau = panneau;
        nbPiles = pileList.size();
    }
}
