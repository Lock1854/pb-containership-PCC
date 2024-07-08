package org.containershipPb;

import java.util.ArrayList;

public class Type {
    int load, unload, num;
    ArrayList<Container> containers;

    public Type(int load, int unload, int num) {
        this.load = load;
        this.unload = unload;
        this.num = num;
        this.containers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "type " + num + " : " + load + " - " + unload + " → " + containers.size() + " container" + (containers.size() != 1 ? "s" : "");
    }
}
