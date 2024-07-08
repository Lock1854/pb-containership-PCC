package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

import static org.containershipPb.Ship.numberPos;
import static org.containershipPb.Ship.positions;
import static org.containershipPb.PbSolver.nbStop;

public class Pile {
    int hauteur;
    ArrayList<Position> posList;
    Bloc bloc;
    boolean isUnder;

    public Pile(int nbPos, Bloc bloc, boolean isUnder){
        this.hauteur = nbPos;
        this.bloc = bloc;
        this.posList = new ArrayList<>();
        this.isUnder = isUnder;
        for (int i = 0; i < nbPos; i++) {
            Position pos = new Position(i, numberPos, false, new IntVar[nbStop], this, isUnder);
            this.posList.add(pos);
            positions.add(pos);
            numberPos++;
        }
    }
}
