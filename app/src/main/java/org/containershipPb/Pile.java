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

    public Pile(int nbPos, Bloc bloc){
        this.hauteur = nbPos;
        this.bloc = bloc;
        this.posList = new ArrayList<>();
        for (int i = 0; i < nbPos; i++) {
            Position pos = new Position(i, numberPos, false, new IntVar[nbStop], this);
            this.posList.add(pos);
            positions.add(pos);
            numberPos++;
        }
    }
}
