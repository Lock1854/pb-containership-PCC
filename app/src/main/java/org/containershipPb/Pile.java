package org.containershipPb;

import java.util.ArrayList;

public class Pile {
    int hauteur;
    ArrayList<Position> posList;
    public Pile(ArrayList<Position> posList){
        this.posList = posList;
        hauteur = posList.size();
    }
}
