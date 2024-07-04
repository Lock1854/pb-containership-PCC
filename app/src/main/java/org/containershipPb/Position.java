package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

public class Position {
    Boolean isPanneau;
    Integer level;
    int number;
    Pile pile;
    Bloc bloc;
    IntVar[] containers;
    Position support;

    public Position(Integer level, int number, Boolean isPanneau, IntVar[] containers, Pile pile){
        this.level = level;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.pile = pile;
        this.support = support();
    }

    public Position(Integer level, int number, Boolean isPanneau, IntVar[] containers, Bloc bloc){
        this.level = level;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.bloc = bloc;
        this.support = support();
    }

    public Position support(){
        if (isPanneau) return null;
        if (level == 0){
            if (pile.bloc.pileListUnder.contains(pile)) return null;
            return pile.bloc.panneau;
        }
        else return pile.posList.get(level - 1);
    }
}
