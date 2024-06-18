package org.containershipPb;

import java.util.ArrayList;

public class Navire {
    final int nbPos, nbBay;
    ArrayList<Bay> bayList;
    public Navire(ArrayList<Bay> bayList){
        this.bayList = bayList;
        nbBay = bayList.size();
        nbPos = nbBay;
    }
}