package org.containershipPb;

import org.chocosolver.solver.Model;

public class PbSolver {
    public static void main(String[] args) {
        Model model = new Model("Chargement navire porte-container");
        Data data = new Data(40, 4, 2, 2, 4, 2);
        Variables variables = new Variables(model, data);
        Contraintes contraintes = new Contraintes(data, model, variables);
        contraintes.postContraints();
    }
}
