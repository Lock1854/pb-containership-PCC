package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class Position {
    Position support;
    ArrayList<Position> bloquant;
    Boolean isPanneau;
    int hauteur, number;
    IntVar[] containers;
    public Position(int hauteur, Boolean isPanneau, IntVar[] containers){
        this.hauteur = hauteur;
        this.isPanneau = isPanneau;
        this.containers = containers;
    }
}
