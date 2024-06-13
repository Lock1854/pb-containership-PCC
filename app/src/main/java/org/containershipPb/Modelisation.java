package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.FixedIntVarImpl;
import org.chocosolver.util.tools.ArrayUtils;

public class Modelisation {
    Model model;
    Data data;
    Position[][] position;
    Container[][] container;
    int[][] planification;
    public Modelisation(){
        model = new Model("Chargement navire porte-container");
        data = new Data(40, 4, 2, 2, 4, 2);
        Generator generator = new Generator(data);
        planification = generator.planification;
        position = new Position[data.nbCont][data.nbStop];
        container = new Container[data.nbPos][data.nbStop];
    }
    public void postContraints(){
        for (int i = 0; i < data.nbStop; i++) {
            DifferentPositions(i);
            for (int c = 0; c < data.nbCont; c++) {
                for (int p = 0; p < data.nbPos; p++) {
                    positionContainerConstraint(p, c, i);
                }
            }
        }
    }
    private void positionContainerConstraint(int p, int c, int i){
        model.ifOnlyIf(
                model.arithm(position[c][i].intVar, "=", p),
                model.arithm(container[p][i].intVar, "=", c)
        );
    }
    private void DifferentPositions(int i){
        Position[] posI = ArrayUtils.getColumn(position, i);
        IntVar[] vars = new FixedIntVarImpl[data.nbCont];
        // find a less complex way to do this
        for (int c = 0; c < data.nbCont; c++) {
            vars[c] = posI[c].intVar;
        }
        model.allDifferent(vars).post();
    }
    private void TransportEveryCont(int i, int p, IntVar I){

    }
//    private void Pile(int i, int p){
//        model.ifThen(
//                model.arithm(position[p][i], "!=", null),
//                model.arithm(position[])
//        );
//    }
}
