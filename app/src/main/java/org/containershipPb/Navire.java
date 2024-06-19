package org.containershipPb;

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