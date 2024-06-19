package org.containershipPb;

import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;

public class Navire {
    final int nbPos, nbPosPan, nbBay;
    ArrayList<Bay> bayList;
    public Navire(ArrayList<Bay> bayList){
        this.bayList = bayList;
        nbBay = bayList.size();
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
    }

    public Navire(int nbBay, int nbBloc, int nbPile, int hMax, int nbStop){
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
        this.bayList = baies;
        this.nbBay = bayList.size();
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
    }

    private int computeNbPos(){
        int n = 0;
        for (Bay bay : bayList) {
            for (Bloc bloc : bay.blocList){
                for (Pile pile : bloc.pileList){
                    n = n + pile.hauteur;
                }
            }
        }
        return n;
    }

    private int computeNbPosPan(){
        int n = nbPos;
        for (Bay bay : bayList){
            n = n + bay.nbBloc;
        }
        return n;
    }
}