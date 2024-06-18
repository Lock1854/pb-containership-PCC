package org.containershipPb;

import java.util.ArrayList;

public class Bay {
    int nbBloc;
    ArrayList<Bloc> blocList;
    public Bay(ArrayList<Bloc> blocList){
        this.blocList = blocList;
        nbBloc = blocList.size();
    }
}
