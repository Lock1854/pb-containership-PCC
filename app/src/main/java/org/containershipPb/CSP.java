package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;

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
        restowTot();
        for (int i = 0; i < nbStop; i++) {
            restow(i);
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
        if (pos.support() != null && !pos.support().isPanneau) {
            model.ifThen(
                    model.arithm(pos.containers[i], "!=", -1),
                    model.arithm(pos.support().containers[i], "!=", -1)
            );
        }
    }

    private void movePos(Position pos, int i){
        // propagation bloquage
        ArrayList<Position> bloquant = pos.bloquant();
        if (bloquant != null) {
            for (Position b : bloquant) {
                model.ifThen(
                        model.arithm(move[pos.number][i], "!=", 0),
                        model.arithm(move[b.number][i], "!=", 0)
                );
            }
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

    private void restow(int i){
        model.sum(ArrayUtils.getColumn(move, i),
                "=",
                restow[i].add(data.nbUnload(i)).add(data.nbLoad(i)).intVar()).post();
    }

    private void restowTot(){
        model.sum(restow, "=", restowTot).post();
    }

    // probably unecessary due to positionContainerEquiv, but maybe helpful ?
    private void DifferentPositions(int i){
        IntVar[] vars = model.intVarArray(nbCont, 0, navire.nbPos);
        int compteur = 0;
        for (Container cont : data.transportedConts(i)) {
            vars[compteur] = cont.positions[i];
            compteur++;
        }
        model.allDifferent(vars).post();
    }

    private void initialisePosVar(){
        for (Position pos : positions){
            if (!pos.isPanneau) {
                pos.containers = new IntVar[nbStop];
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("container[" + i + "]", data.transportedContsNo(i));
                }
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
