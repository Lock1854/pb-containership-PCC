package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.impl.FixedIntVarImpl;
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
        for (int i = 0; i < data.nbStop; i++) {
            IntVar I = model.intVar(i);
            DifferentPositions(i);
            for (int c = 0; c < data.nbCont; c++) {
                TransportEveryCont(c, i, I);
                for (int p = 0; p < data.nbPos; p++) {
                    positionContainerConstraint(p, c, i);
                    pile(p, i);
                    movePos(p, i);
                }
            }
        }
    }
    private void positionContainerConstraint(int p, int c, int i){
        model.ifOnlyIf(
                model.arithm(vars.position[c][i].intVar, "=", p),
                model.arithm(vars.container[p][i].intVar, "=", c)
        );
    }
    private void DifferentPositions(int i){
        Position[] posI = ArrayUtils.getColumn(vars.position, i);
        IntVar[] vars = new FixedIntVarImpl[data.nbCont];
        // find a less complex way to do this
        for (int c = 0; c < data.nbCont; c++) {
            vars[c] = posI[c].intVar;
        }
        model.allDifferent(vars).post();
    }
    private void TransportEveryCont(int c, int i, IntVar I){
        model.ifOnlyIf(
                model.arithm(vars.position[c][i].intVar, "!=", null),
                model.and(
                        model.arithm(I, ">", data.load(c)),
                        model.arithm(I, "<=", data.unload(c))
                )
        );
    }
    private void pile(int p, int i){
        if (!data.supportless.contains(p)) {
            model.ifThen(
                    model.arithm(vars.container[p][i].intVar, "!=", null),
                    model.arithm(vars.container[p-1][i].intVar, "!=", null)
            );
        }
    }
    private void movePos(int p, int i){
        model.ifThen(
                model.and(
                        model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i+1].intVar),
                        model.or(
                                model.arithm(vars.container[p][i].intVar, "=", null),
                                model.arithm(vars.container[p][i+1].intVar, "=", null)
                        )
                ),
                model.arithm(vars.move[p][i].intVar(), "=", 1)
        );
        model.ifThen(
                model.and(
                        model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i+1].intVar),
                        model.and(
                                model.arithm(vars.container[p][i].intVar, "!=", null),
                                model.arithm(vars.container[p][i+1].intVar, "!=", null)
                        )
                ),
                model.arithm(vars.move[p][i].intVar(), "=", 2)
        );

        if (data.supportless.contains(p) && data.hold.contains(p)){
            model.ifThen(
                    model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i+1].intVar),
                    model.arithm(vars.move[p][i].intVar(), "=", 0)
            );
        } else if (data.supportless.contains(p) && !data.hold.contains(p)) {
            model.ifThenElse(
                    model.and(
                            model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i+1].intVar),
                            model.arithm(vars.move[data.pan(p).numero][i], "!=", 0)
                    ),
                    model.arithm(vars.move[p][i].intVar(), "=", 2),
                    model.arithm(vars.move[p][i].intVar(), "=", 0)
            );
        } else if (!data.supportless.contains(p)) {
            model.ifThenElse(
                    model.and(
                            model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i+1].intVar),
                            model.arithm(vars.move[p-1][i], "!=", 0)
                    ),
                    model.arithm(vars.move[p][i].intVar(), "=", 2),
                    model.arithm(vars.move[p][i].intVar(), "=", 0)
            );
        }

        if (data.hold.contains(p)){
            model.ifThen(
                    model.arithm(vars.move[p][i], "!=", 0),
                    model.arithm(vars.move[data.pan(p).numero][i], "!=", 2)
            );
        }
    }
}
