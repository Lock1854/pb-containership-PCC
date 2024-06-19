package org.containershipPb;

import java.util.ArrayList;

public class Pile {
    int hauteur;
    ArrayList<Position> posList;
    public Pile(ArrayList<Position> posList){
        this.posList = posList;
        hauteur = posList.size();
    }

    public Position support(Position pos){
        if (pos.hauteur == 0) return null;
        else return posList.get(hauteur - 1);
    }

    public ArrayList<Position> bloquant(Position pos){

    }
}
