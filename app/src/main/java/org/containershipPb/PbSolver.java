package org.containershipPb;

import org.checkerframework.checker.units.qual.C;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;
import java.util.ArrayList;
import java.util.Arrays;

public class PbSolver {
    static int nbCont = 10, nbStop = 4;
    public static void main(String[] args) {
        Generator generator = new Generator(nbCont, nbStop);
        int[][] planification = generator.planification;
        Model model = new Model("Chargement navire porte-container");
        Navire navire = buildNavire(2, 2, 2, 2);
        Container[] containers = buildConts(planification);
        Variables variables = new Variables(model, navire, nbStop, nbCont);
        Contraintes contraintes;
//        contraintes.postContraints();

        model.getSolver().showStatistics();
        model.getSolver().showSolutions();
        model.getSolver().findOptimalSolution(variables.restowTot, false);
    }
    static private Navire buildNavire(int nbBay, int nbBloc, int nbPile, int hMax){
        ArrayList<Bay> baies = new ArrayList<Bay>();
        for (int bay = 0; bay < nbBay; bay++) {
            ArrayList<Bloc> blocs = new ArrayList<Bloc>();
            for (int bloc = 0; bloc < nbBloc; bloc++) {
                ArrayList<Pile> piles = new ArrayList<Pile>();
                for (int pile = 0; pile <nbPile; pile++) {
                    ArrayList<Position> positions = new ArrayList<Position>();
                    for (int hauteur = 0; hauteur < hMax; hauteur++) {
                        positions.add(new Position(
                                hauteur,
                                hauteur + pile * hauteur + bloc * pile * hauteur + bay * bloc * pile * hauteur,
                                false,
                                new IntVar[nbStop]
                        ));
                    }
                    piles.add(new Pile(positions));
                }
                blocs.add(new Bloc(piles, new Position(
                        null,
                        bloc + nbBloc * nbBay * nbPile * hMax,
                        true,
                        null
                )));
            }
            baies.add(new Bay(blocs));
        }
        return new Navire(baies);
    }
    static private Container[] buildConts(int[][] planification){
        Container[] t = new Container[nbCont];
        for (int c = 0; c <nbCont; c++) {
            t[c] = new Container(planification[c][0], planification[c][1], new IntVar[nbStop]);
        }
        return t;
    }
}
