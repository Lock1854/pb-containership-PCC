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

    public Position(Integer level, int number, Boolean isHatch, IntVar[] containers, Pile pile){
        this.level = level;
        this.isHatch = isHatch;
        this.containers = containers;
        this.number = number;
        this.pile = pile;
        this.support = support();
    }

    public Position(Integer level, int number, Boolean isHatch, IntVar[] containers, Bloc bloc){
        this.level = level;
        this.isHatch = isHatch;
        this.containers = containers;
        this.number = number;
        this.bloc = bloc;
        this.support = support();
    }

    public Position support(){
        if (isHatch) return null;
        if (level == 0){
            if (isUnder()) return null;
            return pile.bloc.hatch;
        }
        else return pile.posList.get(level - 1);
    }

    public Boolean isUnder(){
        return pile.bloc.pileListUnder.contains(pile);
    }
}
