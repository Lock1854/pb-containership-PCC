package org.containershipPb;

import java.util.ArrayList;

public class Navire {
    final int nbPos, nbPosPan, nbBay;
    ArrayList<Bay> bayList;
    static int numberPos = 0, numberPan;
    static ArrayList<Position> positions, panneaux;

    public Navire(ArrayList<Bay> bayList){
        positions = new ArrayList<Position>();
        this.bayList = bayList;
        nbBay = bayList.size();
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
        numberPan = nbPos;
    }

    public Navire(int nbBay, int nbBloc, int nbPileAbove, int nbPileUnder, int nbPosAbove, int nbPosUnder, int nbStop){
        positions = new ArrayList<Position>();
        ArrayList<Bay> baies = new ArrayList<Bay>();
        for (int bay = 0; bay < nbBay; bay++) {
            baies.add(new Bay(nbBloc, nbPileAbove,nbPileUnder, nbPosAbove, nbPosUnder));
            numberPos++;
        }
        this.bayList = baies;
        this.nbBay = bayList.size();
        nbPos = computeNbPos();
        nbPosPan = computeNbPosPan();
        positions.addAll(panneaux);
    }

    private int computeNbPos(){
        int n = 0;
        for (Bay bay : bayList) {
            for (Bloc bloc : bay.blocList){
                for (Pile pile : bloc.pileListAbove){
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