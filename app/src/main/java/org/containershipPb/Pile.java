package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Navire.numberPos;
import static org.containershipPb.Navire.positions;

public class Pile {
    int hauteur;
    ArrayList<Position> posList;
    Bloc bloc;

    public Pile(ArrayList<Position> posList, Bloc bloc){
        this.posList = posList;
        hauteur = posList.size();
        this.bloc = bloc;
    }

    public Pile(int nbPos, Bloc bloc){
        this.hauteur = nbPos;
        this.bloc = bloc;
        this.posList = new ArrayList<Position>();
        for (int i = 0; i < nbPos; i++) {
            Position pos = new Position(i, numberPos, false, null, this);
            this.posList.add(pos);
            positions.add(pos);
            numberPos++;
        }
    }

    public Position support(Position pos){
        if (pos.hauteur == 0) return null;
        else return posList.get(hauteur - 1);
    }
}
