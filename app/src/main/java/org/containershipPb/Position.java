package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import static org.containershipPb.PbSolver.nbBay;
import static org.containershipPb.PbSolver.nbBloc;
import static org.containershipPb.Ship.*;

public class Position {
    Boolean isHatch;
    Integer level;
    int number;
    Pile pile;
    Bloc bloc;
    IntVar[] containers;
    Position support;
    boolean isUnder;

    public Position(Integer level, int number, boolean isHatch, IntVar[] containers, Pile pile, boolean isUnder){
        this.level = level;
        this.isHatch = isHatch;
        this.containers = containers;
        this.number = number;
        this.pile = pile;
        this.isUnder = isUnder;
        this.support = support();
        if (level == 0) {
            if (isUnder) pile.bloc.bottomPosUnder.get(level).add(this);
            else pile.bloc.bottomPosAbove.get(level).add(this);
        }
        if (nbBloc > 1 && !isUnder && posInBloc == 0) {
            symPosBloc.get(posInBloc).add(this);
            posInBloc++;
        }
        if (nbBay > 1 && !isUnder && posInBay == 0) {
            symPosBay.get(posInBay).add(this);
            posInBay++;
        }
    }

    public Position(Integer level, int number, boolean isHatch, IntVar[] containers, Bloc bloc){
        this.level = level;
        this.isHatch = isHatch;
        this.containers = containers;
        this.number = number;
        this.bloc = bloc;
        this.support = support();
        this.isUnder = false;
    }

    public Position support(){
        if (isHatch) return null;
        if (level == 0){
            if (isUnder) return null;
            return pile.bloc.hatch;
        }
        else return pile.posList.get(level - 1);
    }
}
