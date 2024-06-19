package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;

public class Position {
    Position support;
    ArrayList<Position> bloquant;
    Boolean isPanneau;
    Integer hauteur;
    int number;
    IntVar[] containers;
    public Position(Integer hauteur, int number, Boolean isPanneau, IntVar[] containers){
        this.hauteur = hauteur;
        this.isPanneau = isPanneau;
        this.containers = containers;
        this.number = number;
    }
}
