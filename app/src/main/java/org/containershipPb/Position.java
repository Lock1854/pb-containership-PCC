package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;

public class Position {
    Boolean isPanneau;
    Integer level;
    int number;
    Pile pile;
    Bloc bloc;
    IntVar[] containers;

    public Position(Integer level, int number, Boolean isPanneau, IntVar[] containers, Pile pile){
        this.level = level;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.pile = pile;
    }

    public Position(Integer level, int number, Boolean isPanneau, IntVar[] containers, Bloc bloc){
        this.level = level;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.bloc = bloc;
    }

    public Position support(){
        if (isPanneau) return null;
        if (level == 0){
            if (pile.bloc.pileListUnder.contains(pile)) return null;
            return pile.bloc.panneau;
        }
        else return pile.posList.get(level - 1);
    }

    public ArrayList<Position> bloquant(){
        ArrayList<Position> L = new ArrayList<>();
        if (isPanneau){
            for (Pile pile : bloc.pileListAbove){
                L.add(pile.posList.getFirst());
            }
        } else if (pile.bloc.pileListAbove.contains(pile)){
            if (level == pile.hauteur - 1) return null;
            else L.add(pile.posList.get(level + 1));
        } else {
            L.add(pile.bloc.panneau);
            if (level != pile.hauteur - 1) L.add(pile.posList.get(level + 1));
        }
        return L;
    }
}
