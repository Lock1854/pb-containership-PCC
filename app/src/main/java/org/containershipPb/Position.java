package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

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
            if (isUnder) pile.bloc.bottomPosUnder.add(this);
            else pile.bloc.bottomPosAbove.add(this);
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
