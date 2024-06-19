package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Contraintes {
    Navire navire;
    Model model;
    Variables vars;
    int nbCont, nbStop;
    public Contraintes(Navire navire, Model model, Variables vars, int nbStop, int nbCont){
        this.navire = navire;
        this.model = model;
        this.vars = vars;
        this.nbStop = nbStop;
        this.nbCont = nbCont;
    }

//    public void postContraints(){
//        restowTot();
//        for (int i = 0; i < nbStop; i++) {
//            DifferentPositions(i);
//            restow(i);
//            for (int p = 0; p < navire.nbPos; p++) {
//                pile(p, i);
//                movePos(p, i);
//                for (int c = 0; c < nbCont; c++) {
//                    positionContainerConstraint(p, c, i);
//                }
//            }
//        }
//    }
//    private void positionContainerConstraint(Position pos, Container cont, int i){
//        if (cont.positions[i] != null) {
//            model.ifOnlyIf(
//                    model.arithm(cont.positions[i], "=", pos.number),
//                    model.arithm(pos.containers[i], "=", cont.number)
//            );
//        } else model.arithm(pos.containers[i], "!=", cont.number).post();
//    }
//    private void DifferentPositions(int i){
//        Position[] posI = ArrayUtils.getColumn(vars.position, i);
//        IntVar[] vars = model.intVarArray(nbCont, 0, navire.nbPosPan);
//        for (int c = 0; c < nbCont; c++) {
//            if (posI[c] != null) vars[c] = posI[c].intVar;
//        }
//        model.allDifferent(vars).post();
//    }
//
//    private void pile(Position pos, int i){
//        if (!navire.supportless.contains(p)) {
//            model.ifThen(
//                    model.arithm(vars.container[p][i].intVar, "!=", -1),
//                    model.arithm(vars.container[p-1][i].intVar, "!=", -1)
//            );
//        }
//    }
//    private void movePos(int p, int i){
//        if (i < nbStop - 1) {
//            // cas cont(p,i) != cont(p,i+1)
//            model.ifThen(
//                    model.and(
//                            model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i + 1].intVar),
//                            model.or(
//                                    model.arithm(vars.container[p][i].intVar, "=", -1),
//                                    model.arithm(vars.container[p][i + 1].intVar, "=", -1)
//                            )
//                    ),
//                    model.arithm(vars.move[p][i].intVar(), "=", 1)
//            );
//            model.ifThen(
//                    model.and(
//                            model.arithm(vars.container[p][i].intVar, "!=", vars.container[p][i + 1].intVar),
//                            model.and(
//                                    model.arithm(vars.container[p][i].intVar, "!=", -1),
//                                    model.arithm(vars.container[p][i + 1].intVar, "!=", -1)
//                            )
//                    ),
//                    model.arithm(vars.move[p][i].intVar(), "=", 2)
//            );
//            // cas cont(p,i) = cont(p,i+1)
//            if (navire.supportless.contains(p) && navire.hold.contains(p)) {
//                model.ifThen(
//                        model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
//                        model.arithm(vars.move[p][i].intVar(), "=", 0)
//                );
//            } else if (navire.supportless.contains(p) && !navire.hold.contains(p)) {
//                model.ifThenElse(
//                        model.and(
//                                model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
//                                model.arithm(vars.move[navire.pan(p).numero][i], "=", 0)
//                        ),
//                        model.arithm(vars.move[p][i].intVar(), "=", 0),
//                        model.arithm(vars.move[p][i].intVar(), "=", 2)
//                );
//            } else if (!navire.supportless.contains(p)) {
//                model.ifThenElse(
//                        model.and(
//                                model.arithm(vars.container[p][i].intVar, "=", vars.container[p][i + 1].intVar),
//                                model.arithm(vars.move[p - 1][i], "=", 0)
//                        ),
//                        model.arithm(vars.move[p][i].intVar(), "=", 0),
//                        model.arithm(vars.move[p][i].intVar(), "=", 2)
//                );
//            }
//        }else {
//            // cas de la dernière étape
//            model.ifThenElse(
//                    model.arithm(vars.container[p][i].intVar, "=", -1),
//                    model.arithm(vars.move[p][i], "=", 0),
//                    model.arithm(vars.move[p][i], "=", 1)
//            );
//        }
//        // cas des panneaux
//        if (navire.hold.contains(p)){
//            model.ifThen(
//                    model.arithm(vars.move[p][i], "!=", 0),
//                    model.arithm(vars.move[navire.pan(p).numero][i], "=", 2)
//            );
//        }
//    }
//    private void restow(int i){
//        model.sum(ArrayUtils.getColumn(vars.move, i),
//                "=",
//                vars.restow[i].add(navire.nbUnload(i)).add(navire.nbLoad(i)).intVar()).post();
//    }
//    private void restowTot(){
//        model.sum(vars.restow, "=", vars.restowTot).post();
//    }
}
