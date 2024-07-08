package org.containershipPb;

import java.util.ArrayList;

import static org.containershipPb.Ship.halfBlocs;

public class Bay {
    int nbBloc;
    ArrayList<Bloc> blocList;

    public Bay(int nbBloc, int nbPileAbove, int nbPilesUnder, int nbPosAbove, int nbPosUnder){
        this.nbBloc = nbBloc;
        this.blocList = new ArrayList<>();
        for (int i = 0; i < nbBloc; i++) {
            Bloc bloc = new Bloc(nbPileAbove, nbPilesUnder, nbPosAbove, nbPosUnder, this);
            this.blocList.add(bloc);
            halfBlocs.add(bloc.bottomPosUnder);
            halfBlocs.add(bloc.bottomPosAbove);
        }
    }
}
