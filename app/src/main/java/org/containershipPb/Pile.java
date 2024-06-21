package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Navire.numberPos;
import static org.containershipPb.Navire.positions;

public class Pile {
    int hauteur;
    ArrayList<Position> posList;
    Bloc bloc;

    public Pile(int nbPos, Bloc bloc){
        this.hauteur = nbPos;
        this.bloc = bloc;
        this.posList = new ArrayList<>();
        for (int i = 0; i < nbPos; i++) {
            Position pos = new Position(i, numberPos, false, null, this);
            this.posList.add(pos);
            positions.add(pos);
            numberPos++;
        }
    }
}
