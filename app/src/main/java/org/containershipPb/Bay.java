package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Navire.numberPos;

public class Bay {
    int nbBloc;
    ArrayList<Bloc> blocList;

    public Bay(int nbBloc, int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder){
        this.nbBloc = nbBloc;
        this.blocList = new ArrayList<Bloc>();
        for (int i = 0; i < nbBloc; i++) {
            this.blocList.add(new Bloc(nbPileAbove, nbPilesUnder, nbPosAbove, nbPosUnder, this));
            numberPos++;
        }
    }
}
