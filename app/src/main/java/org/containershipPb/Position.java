package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;

public class Position {
    ArrayList<Position> bloquant;
    Position support;
    Boolean isPanneau;
    Integer hauteur;
    int number;
    Pile pile;
    Bloc bloc;
    IntVar[] containers;

    public Position(Integer hauteur, int number, Boolean isPanneau, IntVar[] containers, Pile pile){
        this.hauteur = hauteur;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.pile = pile;
        this.support = support();
        this.bloquant = bloquant();
    }

    public Position(Integer hauteur, int number, Boolean isPanneau, IntVar[] containers, Bloc bloc){
        this.hauteur = hauteur;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
        this.bloc = bloc;
        this.support = support();
        this.bloquant = bloquant();
    }

    private Position support(){
        if (isPanneau) return null;
        if (hauteur == 0){
            if (pile.bloc.pileListUnder.contains(pile)) return null;
            else return pile.bloc.panneau;
        }
        else return pile.posList.get(hauteur - 1);
    }

    private ArrayList<Position> bloquant(){
        ArrayList<Position> L = new ArrayList<>();
        if (isPanneau){
            for (Pile pile : bloc.pileListAbove){
                L.add(pile.posList.getFirst());
            }
        } else if (pile.bloc.pileListAbove.contains(pile)){
            if (hauteur == pile.hauteur - 1) return null;
            else L.add(pile.posList.get(hauteur + 1));
        } else {
            L.add(pile.bloc.panneau);
            if (hauteur != pile.hauteur - 1) L.add(pile.posList.get(hauteur + 1));
        }
        return L;
    }
}
