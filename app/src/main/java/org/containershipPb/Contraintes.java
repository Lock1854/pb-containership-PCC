package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Contraintes {
    Data data;
    Model model;
    Variables vars;

    public Contraintes(Data data, Model model, Variables vars){
        this.data = data;
        this.model = model;
        this.vars = vars;
    }

    public void postContraints(){
        restowTot();
        for (int i = 0; i < data.nbStop; i++) {
            DifferentPositions(i);
            restow(i);
            for (int p = 0; p < data.nbPos; p++) {
                pile(p, i);
                movePos(p, i);
                for (int c = 0; c < data.nbCont; c++) {
                    positionContainerConstraint(p, c, i);
//                    noRestow(c, i);
                }
            }
        }
    }

    private void positionContainerConstraint(int p, int c, int i){
        if (vars.position[c][i] != null) {
            model.ifOnlyIf(
                    model.arithm(vars.position[c][i].intVar, "=", p),
                    model.arithm(vars.container[p][i].intVar, "=", c)
            );
        }
        else model.arithm(vars.container[p][i].intVar, "!=", c).post();
    }

    private void DifferentPositions(int i){
        Position[] posI = ArrayUtils.getColumn(vars.position, i);
        IntVar[] vars = model.intVarArray(data.nbCont, 0, data.nbPosPan);
        for (int c = 0; c < data.nbCont; c++) {
            if (posI[c] != null) vars[c] = posI[c].intVar;
        }
        model.allDifferent(vars).post();
    }

    private void pile(int p, int i){
        if (!data.supportless.contains(p)) {
            model.ifThen(
                    model.arithm(vars.container[p][i].intVar, "!=", -1),
                    model.arithm(vars.container[p-1][i].intVar, "!=", -1)
            );
        }
    }

    private void movePos(int p, int i){
        if (i < data.nbStop - 1) {
            // cas cont(p,i) != cont(p,i+1)
            model.ifThen(
                    model.and(
                            model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i + 1].intVar),
                            model.or(
                                    model.arithm(vars.container[p][i].intVar, "=", -1),
                                    model.arithm(vars.container[p][i + 1].intVar, "=", -1)
                            )
                    ),
                    model.arithm(vars.move[p][i], "=", 1)
            );
            model.ifThen(
                    model.and(
                            model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i + 1].intVar),
                            model.and(
                                    model.arithm(vars.container[p][i].intVar, "!=", -1),
                                    model.arithm(vars.container[p][i + 1].intVar, "!=", -1)
                            )
                    ),
                    model.arithm(vars.move[p][i], "=", 2)
            );
            // cas cont(p,i) = cont(p,i+1)
            if (data.supportless.contains(p) && data.hold.contains(p)) {
                model.ifThen(
                        model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
                        model.arithm(vars.move[p][i], "=", 0)
                );
            } else if (data.supportless.contains(p) && !data.hold.contains(p)) {
                model.ifThenElse(
                        model.and(
                                model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
                                model.arithm(vars.move[data.pan(p).numero][i], "=", 0)
                        ),
                        model.arithm(vars.move[p][i], "=", 0),
                        model.arithm(vars.move[p][i], "=", 2)
                );
            } else if (!data.supportless.contains(p)) {
                model.ifThenElse(
                        model.and(
                                model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
                                model.arithm(vars.move[p - 1][i], "=", 0)
                        ),
                        model.arithm(vars.move[p][i], "=", 0),
                        model.arithm(vars.move[p][i], "=", 2)
                );
            }
        }else {
            // cas de la dernière étape
            model.ifThenElse(
                    model.arithm(vars.container[p][i].intVar, "=", -1),
                    model.arithm(vars.move[p][i], "=", 0),
                    model.arithm(vars.move[p][i], "=", 1)
            );
        }
        // cas des panneaux
        if (data.hold.contains(p)){
            model.ifThen(
                    model.arithm(vars.move[p][i], "!=", 0),
                    model.arithm(vars.move[data.pan(p).numero][i], "=", 2)
            );
        }
    }

    private void noRestow(int c, int i){
        if (i == data.nbStop - 1) return;
        if (vars.position[c][i] != null && vars.position[c][i+1] != null){
            model.arithm(vars.position[c][i].intVar, "=", vars.position[c][i+1].intVar).post();
        }
    }

    private void restow(int i){
        model.sum(ArrayUtils.getColumn(vars.move, i),
                "=",
                vars.restow[i].add(data.nbUnload(i)).add(data.nbLoad(i)).intVar()).post();
    }

    private void restowTot(){
        model.sum(vars.restow, "=", vars.restowTot).post();
    }
}
