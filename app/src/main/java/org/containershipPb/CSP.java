package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import static org.containershipPb.Navire.positions;
import static org.containershipPb.PbSolver.*;

public class CSP {
    Model model;
    Navire navire;
    Data data;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;
    Boolean restowAllowed, table;
    TupleGenerator tupleGen;

    public CSP(Model model, Navire navire, Data data, Boolean restowAllowed, Boolean table){
        this.model = model;
        this.navire = navire;
        this.data = data;
        this.restowAllowed = restowAllowed;
        this.table = table;
        move = model.intVarMatrix("move", navire.nbPosPan, nbStop, 0, 2, false);
        nbVar += navire.nbPosPan * nbStop;
        if (restowAllowed) {
            restow = model.intVarArray("restow", nbStop, 0, navire.nbPosPan);
            restowTot = model.intVar("restowTot", 0, nbStop * navire.nbPosPan);
            nbVar += nbStop + 1;
        }
        if (table) tupleGen = new TupleGenerator(data);
        initialisePosVar();
    }

    public void solve(String usageSolution) {
        postContraints();
        model.getSolver().showStatistics();
        Solver solver = model.getSolver();
        if (usageSolution.equals("show")) solver.showSolutions();
        Solution solution = restowAllowed? model.getSolver().findOptimalSolution(restowTot, false)
                : solver.findSolution();
        if (usageSolution.equals("print")) printSolution(solution);
    }

    public void postContraints(){
        if (restowAllowed) computeRestowTot();
        for (int i = 0; i < nbStop; i++) {
            forceDifferentPositions(i);
            ensureAllTransported(i);
            if (restowAllowed) computeRestow(i);
            for (Position pos : positions) {
                ensureStack(pos, i);
                computeMove(pos, i);
            }
        }
    }

    private void forceDifferentPositions(int i){
        model.allDifferent(getAllContent(i)).post();
    }

    private void ensureAllTransported(int i){
        IntVar[] t = model.intVarArray(data.onboardConts[i].size(), new int[]{1});
        model.globalCardinality(getAllContent(i), data.onboardContsNo(i), t, false).post();
    }

    private void ensureStack(Position pos, int i){
        if (pos.support() != null && !pos.support().isPanneau) {
            if (table){
                model.table(pos.containers[i], pos.support().containers[i], tupleGen.getPile()).post();
            } else {
                model.ifThen(
                        model.arithm(pos.containers[i], "!=", - pos.number),
                        model.arithm(pos.support().containers[i], "!=", - pos.number)
                );
            }
        }
    }

    private void computeMove(Position pos, int i){
        if (table) {
            if (i == nbStop - 1) {
                model.table(
                        new IntVar[]{pos.containers[i], move[pos.number][i]},
                        tupleGen.getMovePos(true, false)
                ).post();
            } else if (pos.support() == null){
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], model.intVar(0)},
                        tupleGen.getMovePos(false, false)
                ).post();
            } else {
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], move[pos.support().number][i]},
                        tupleGen.getMovePos(false, true)
                ).post();
            }
            if (pos.pile.bloc.pileListUnder.contains(pos.pile)) {
                model.table(
                        new IntVar[]{move[pos.number][i], move[pos.pile.bloc.panneau.number][i]},
                        tupleGen.getMovePan()
                ).post();
            }
        } else if (pos.isPanneau) return;
        else {
            if (i == nbStop - 1) {
                // cas de la dernière étape
                model.ifThenElse(
                        model.arithm(pos.containers[i], "=", - pos.number),
                        model.arithm(move[pos.number][i], "=", 0),
                        model.arithm(move[pos.number][i], "=", 1)
                );
            } else {
                // cas cont(p,i) != cont(p,i+1) et l'un des deux nul
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                                model.or(
                                        model.arithm(pos.containers[i], "=", - pos.number),
                                        model.arithm(pos.containers[i + 1], "=", - pos.number)
                                )
                        ),
                        model.arithm(move[pos.number][i], "=", 1)
                );
                // cas cont(p,i) != cont(p,i+1) et aucun des deux nuls
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                                model.arithm(pos.containers[i], "!=", - pos.number),
                                model.arithm(pos.containers[i + 1], "!=", - pos.number)
                        ),
                        model.arithm(move[pos.number][i], "=", 2)
                );
                // cas cont(p,i) = cont(p,i+1) = null
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                model.arithm(pos.containers[i], "=", - pos.number)
                        ),
                        model.arithm(move[pos.number][i], "=", 0)
                );
                // cas cont(p,i) = cont(p,i+1) != null
                if (pos.support() == null) {
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", - pos.number)
                            ),
                            model.arithm(move[pos.number][i], "=", 0)
                    );
                } else {
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", - pos.number),
                                    model.arithm(move[pos.support().number][i], "=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 0)
                    );
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", - pos.number),
                                    model.arithm(move[pos.support().number][i], "!=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 2)
                    );
                }
            }
            // propagation bloquage
            if (pos.pile.bloc.pileListUnder.contains(pos.pile)) {
                model.ifThen(
                        model.arithm(move[pos.number][i], "!=", 0),
                        model.arithm(move[pos.pile.bloc.panneau.number][i], "=", 2)
                );
            }
        }
    }

    private void computeRestow(int i){
        model.sum(ArrayUtils.getColumn(move, i),
                "=",
                restow[i].add(data.nbUnload(i)).add(data.nbLoad(i)).intVar()
        ).post();
    }

    private void computeRestowTot(){
        model.sum(restow, "=", restowTot).post();
    }

//    private void forbidRestow(Position pos, Container cont, int i){
//        if (i == nbStop - 1) return;
//        else {
//            model.ifThen(
//                    model.and(
//                            model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
//                            model.arithm(pos.containers[i], "!=", - pos.number)
//                    ),
//
//            );
//        }
//    }

    private void initialisePosVar(){
        for (Position pos : positions){
            if (pos.isPanneau){
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", new int[]{- pos.number});
                    nbVar++;
                }
            }
            else {
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", data.onboardContsNo(i, pos));
                    nbVar++;
                }
            }
        }
    }

    private IntVar[] getAllContent(int i){
        IntVar[] vars = new IntVar[positions.size()];
        int compteur = 0;
        for (Position pos : positions) {
            vars[compteur] = pos.containers[i];
            compteur++;
        }
        return vars;
    }

    private void printSolution(Solution solution){
        if(solution == null) System.out.println("No solution found");
        else {
            System.out.print("\n");
            for (int p = 0; p < navire.nbPos; p++)  {
                for (int i = 0; i < nbStop; i++) {
                    if (solution.getIntVal(positions.get(p).containers[i]) > 0) {
                        System.out.print("cont(" + positions.get(p).number + "," + i + ") = " + solution.getIntVal(positions.get(p).containers[i]) + " ; ");
                    } else {
                        System.out.print("cont(" + positions.get(p).number + "," + i + ") = null ; ");
                    }
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            for (int c = 0; c < navire.nbPosPan; c++) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("move(" + c + "," + i + ") = " + solution.getIntVal(move[c][i]) + " ; ");
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            if (restowAllowed) {
                for (int i = 0; i < nbStop; i++) {
                    System.out.print("restow[" + i + "] = " + solution.getIntVal(restow[i]) + " ; ");
                }
            }
        }
    }
}
