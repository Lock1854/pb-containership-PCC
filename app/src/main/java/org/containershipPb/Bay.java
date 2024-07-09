package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Ship.*;

public class Bay {
    int nbBloc;
    ArrayList<Bloc> blocList;

    public Bay(int nbBloc, int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder){
        posInBay = 0;
        this.nbBloc = nbBloc;
        this.blocList = new ArrayList<>();
        for (int i = 0; i < nbBloc; i++) {
            Bloc bloc = new Bloc(nbPileAbove, nbPilesUnder, nbPosAbove, nbPosUnder, this);
            this.blocList.add(bloc);
            symPosStack.addAll(bloc.bottomPosUnder);
            symPosStack.addAll(bloc.bottomPosAbove);
        }
    }
}
