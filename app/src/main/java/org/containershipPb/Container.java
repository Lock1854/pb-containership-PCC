package org.containershipPb;

public class Container {
    Position position;
    int load, unload, numero;
    Data data;
    public Container(int numero, Data data, int[][] planification) {
        this.data = data;
        this.numero = numero;
        this.load = planification[numero][0];
        this.unload = planification[numero][1];
    }
}
