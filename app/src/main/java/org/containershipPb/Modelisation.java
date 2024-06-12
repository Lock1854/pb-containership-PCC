package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Modelisation {
    Model model;
    Data data;
    IntVar[][] position;
    int[][] planification;
    public Modelisation(){
        model = new Model("Chargement navire porte-container");
        data = new Data(40, 4, 2, 2, 4, 2);
        Generator generator = new Generator(data);
        planification = generator.planification;
        position = model.intVarMatrix("position", data.nbPos,  data.nbStop, 0, data.nbCont, false);
    }
    public void postContraints(){
        for (int i = 0; i < data.nbStop; i++) {
            IntVar I = model.intVar(i);
            DifferentPositions(i).post();
            for (int p = 0; p < data.nbCont; p++) {
                TransportEveryCont(i, p, I);
            }
        }

    }
    private Constraint DifferentPositions(int i){
            return model.allDifferent(ArrayUtils.getColumn(position, i));
    }
    private void TransportEveryCont(int i, int p, IntVar I){
        for (int c = 0; c < data.nbCont; c++){
            model.ifThen(
                    model.arithm(position[p][i], "=", c),
                    model.and(
                            model.arithm(I, ">", planification[c][0]),
                            model.arithm(I, ">=", planification[c][1])
                    )
            );
        }
    }
//    private void Pile(int i, int p){
//        model.ifThen(
//                model.arithm(position[p][i], "!=", null),
//                model.arithm(position[])
//        );
//    }
//    private void positionContainerConstraint(){
//        for (int p = 0; p < data.nbPos; p++) {
//            for (int c = 0; c < data.nbCont; c++) {
//                for (int i = 0; i < data.nbStop; i++) {
//                    model.ifOnlyIf(
//                            model.arithm(position[c][i], "=", p),
//                            model.arithm(container[p][i], "=", c)
//                    );
//                }
//            }
//        }
//    }
}
