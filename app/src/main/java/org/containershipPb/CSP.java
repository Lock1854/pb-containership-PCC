package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import static org.containershipPb.Data.containers;
import static org.containershipPb.Navire.positions;
import static org.containershipPb.PbSolver.nbCont;
import static org.containershipPb.PbSolver.nbStop;

public class CSP {
    Model model;
    Navire navire;
    Data data;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;

    public CSP(Model model, Navire navire, Data data){
        this.model = model;
        this.navire = navire;
        this.data = data;
        move = model.intVarMatrix("move", navire.nbPosPan, nbStop, 0, 2, false);
        restow = model.intVarArray("restow", nbStop, 0, navire.nbPos);
        restowTot = model.intVar("restowTot", 0, nbCont);
        initialisePosVar();
        initialiseContVar();
    }

        public void postContraints(){
        for (int i = 0; i < nbStop; i++) {
            for (Position pos : positions) {
                pile(pos, i);
                movePos(pos, i);
                for (Container cont : containers) {
                    positionContainerEquiv(pos, cont, i);
                }
            }
        }
    }
    private void positionContainerEquiv(Position pos, Container cont, int i){
        if (!pos.isPanneau){
        if (cont.positions[i] != null) {
            model.ifOnlyIf(
                    model.arithm(cont.positions[i], "=", pos.number),
                    model.arithm(pos.containers[i], "=", cont.number)
            );
        } else model.arithm(pos.containers[i], "!=", cont.number).post();
        }
    }

    private void pile(Position pos, int i){
        if (pos.support != null && !pos.support.isPanneau) {
            model.ifThen(
                    model.arithm(pos.containers[i], "!=", -1),
                    model.arithm(pos.support.containers[i], "!=", -1)
            );
        }
    }

    private void movePos(Position pos, int i){
        // propagation bloquage
        for (Position bloquant : pos.bloquant) {
            model.ifThen(
                    model.arithm(move[pos.number][i], "!=", 0),
                    model.arithm(move[bloquant.number][i], "!=", 0)
            );
        }
        if (!pos.isPanneau) {
            if (i == nbStop - 1) {
                // cas de la dernière étape
                model.ifThenElse(
                        model.arithm(pos.containers[i], "=", -1),
                        model.arithm(move[pos.number][i], "=", 0),
                        model.arithm(move[pos.number][i], "=", 1)
                );
            } else {
                // cas cont(p,i) != cont(p,i+1)
                model.ifThenElse(
                        model.and(
                                model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                                model.or(
                                        model.arithm(pos.containers[i], "=", -1),
                                        model.arithm(pos.containers[i + 1], "=", -1)
                                )
                        ),
                        model.arithm(move[pos.number][i], "=", 1),
                        model.arithm(move[pos.number][i], "=", 2)
                );
                // cas cont(p,i) = cont(p,i+1)
                model.ifThen(
                        model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                        model.arithm(move[pos.number][i], "!=", 1)
                );
            }
        }
    }

//    private void restow(int i){
//        model.sum(ArrayUtils.getColumn(vars.move, i),
//                "=",
//                vars.restow[i].add(navire.nbUnload(i)).add(navire.nbLoad(i)).intVar()).post();
//    }
//    private void restowTot(){
//        model.sum(vars.restow, "=", vars.restowTot).post();
//    }

    // probably unecessary due to positionContainerEquiv, but maybe helpful ?
//    private void DifferentPositions(int i){
//        Position[] posI = ArrayUtils.getColumn(vars.position, i);
//        IntVar[] vars = model.intVarArray(nbCont, 0, navire.nbPosPan);
//        for (int c = 0; c < nbCont; c++) {
//            if (posI[c] != null) vars[c] = posI[c].intVar;
//        }
//        model.allDifferent(vars).post();
//    }

    private void initialisePosVar(){
        for (Position pos : positions){
            for (int i = 0; i < pos.containers.length; i++) {
                pos.containers[i] = model.intVar("container[" + i + "]", data.transportedContsNo(i));
            }
        }
    }

    private void initialiseContVar(){
        for (Container cont : containers){
            for (int i = 0; i < cont.positions.length; i++) {
                if (i > cont.load && i <= cont.unload) {
                    cont.positions[i] = model.intVar("position[" + i + "]", 0, nbCont);
                } else cont.positions[i] = null;
            }
        }
    }
}
