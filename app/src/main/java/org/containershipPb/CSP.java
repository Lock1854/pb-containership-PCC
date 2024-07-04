package org.containershipPb;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.propagation.PropagationProfiler;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;

import static org.containershipPb.Ship.positions;
import static org.containershipPb.PbSolver.*;

public class CSP {
    Model model;
    Ship ship;
    Data data;
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;
    Boolean restowAllowed, table;
    TupleGenerator tupleGen;
    ArrayList<IntVar> allIntVar = new ArrayList<>();

    public CSP(Model model, Ship ship, Data data, Boolean restowAllowed, Boolean table){
        this.model = model;
        this.ship = ship;
        this.data = data;
        this.restowAllowed = restowAllowed;
        this.table = table;
        initialiseContVar();
        move = model.intVarMatrix("move", ship.nbPosPan, nbStop, 0, 2, false);
        allIntVar.addAll(getAllMove());
        nbVar += ship.nbPosPan * nbStop;
        if (restowAllowed) {
            restow = model.intVarArray("restow", nbStop, 0, 2, false);
            allIntVar.addAll(Arrays.stream(restow).toList());
            restowTot = model.intVar("restowTot", 0, nbStop * ship.nbPosPan, false);
            allIntVar.add(restowTot);
            nbVar += nbStop + 1;
        }
        if (table) tupleGen = new TupleGenerator(data);
    }

    public void solve(String usageSolution) {
        postContraints();
        Solver solver = model.getSolver();
        solver.showStatistics();
        solver.observePropagation(new PropagationProfiler(model));
        solver.setSearch(new IntStrategy(
                allIntVar.toArray(new IntVar[0]),
                new InputOrder<>(model),
                new IntDomainMin()
        ));
        if (usageSolution.equals("show")) solver.showSolutions();
        Solution solution;
        if (restowAllowed) solution = model.getSolver().findOptimalSolution(restowTot, false);
        else solution = solver.findSolution();
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
                if (!restowAllowed) forbidRestow(pos, i);
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
        if (pos.support != null && !pos.support.isPanneau) {
            if (table){
                model.table(pos.containers[i], pos.support.containers[i], tupleGen.getStackTuple(pos)).post();
            } else {
                model.ifThen(
                        model.arithm(pos.containers[i], "!=", - pos.number),
                        model.arithm(pos.support.containers[i], "!=", - pos.support.number)
                );
            }
        }
    }

    private void computeMove(Position pos, int i){
        if (table && !pos.isPanneau) {
            if (i == nbStop - 1) {
                model.table(
                        new IntVar[]{pos.containers[i], move[pos.number][i]},
                        tupleGen.getMovePosTuple(pos, true)
                ).post();
            } else if (pos.support == null){
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], model.intVar(0)},
                        tupleGen.getMovePosTuple(pos, false)
                ).post();
            } else {
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], move[pos.support.number][i]},
                        tupleGen.getMovePosTuple(pos, false)
                ).post();
            }
            if (!pos.isPanneau && pos.isUnder()) {
                model.table(
                        new IntVar[]{move[pos.number][i], move[pos.pile.bloc.hatch.number][i]},
                        tupleGen.getMovePanTuple()
                ).post();
            }
        }
        else if (!pos.isPanneau){
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
                if (pos.support == null) {
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
                                    model.arithm(move[pos.support.number][i], "=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 0)
                    );
                    model.ifThen(
                            model.and(
                                    model.arithm(pos.containers[i], "=", pos.containers[i + 1]),
                                    model.arithm(pos.containers[i], "!=", - pos.number),
                                    model.arithm(move[pos.support.number][i], "!=", 0)
                            ),
                            model.arithm(move[pos.number][i], "=", 2)
                    );
                }
            }
            // propagation bloquage
            if (pos.isUnder()) {
                model.ifThen(
                        model.arithm(move[pos.number][i], "!=", 0),
                        model.arithm(move[pos.pile.bloc.hatch.number][i], "=", 2)
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

    private void forbidRestow(Position pos, int i) {
        if (i < nbStop - 1) {
            model.ifThen(
                    model.and(
                            model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                            model.arithm(pos.containers[i], "!=", -pos.number)
                    ),
                    model.member(pos.containers[i], data.Unload(i))
            );
            model.ifThen(
                    model.and(
                            model.arithm(pos.containers[i], "!=", pos.containers[i + 1]),
                            model.arithm(pos.containers[i + 1], "!=", -pos.number)
                    ),
                    model.member(pos.containers[i+1], data.Load(i))
            );
        }
    }

    private void initialiseContVar(){
        for (Position pos : positions){
            if (pos.isPanneau){
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", new int[]{- pos.number});
                    nbVar++;
                    allIntVar.add(pos.containers[i]);
                }
            }
            else {
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", data.onboardContsNo(i, pos));
                    nbVar++;
                    allIntVar.add(pos.containers[i]);
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

    private ArrayList<IntVar> getAllMove(){
        ArrayList<IntVar> L = new ArrayList<>();
        for (int i = 0; i < nbStop; i++) {
            for (int p = 0; p < positions.size(); p++) {
                L.add(move[p][i]);
            }
        }
        return L;
    }

    private void printSolution(Solution solution){
        if(solution == null) System.out.println("No solution found");
        else {
            Position[] printOrderedPos = getPrintOrderedPos();
            System.out.print("\n");
            for (int i = 0; i < nbStop; i++) {
                System.out.printf("%s %d %s", "Étape", i, "--------------------------------\n");
                int compteur = 0;
                for (int b = 0; b < nbBay; b++) {
                    compteur = printPiles(solution, printOrderedPos, i, compteur, true);
                    printSeparator("----", " |");
                    compteur = printPiles(solution, printOrderedPos, i, compteur, false);
                    printSeparator("====", "==");
                }
            }
            System.out.print("\n");
            for (int c = 0; c < ship.nbPosPan; c++) {
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

    private int printPiles(Solution solution, Position[] printOrderedPos, int i, int compteur, Boolean isAbove) {
        int pileLim; int posLim; int compt = compteur;
        if (isAbove) {
            pileLim = nbPileAbove;
            posLim = nbPosAbove;
        } else {
            pileLim = nbPileUnder;
            posLim = nbPosUnder;
        }
        for (int lu = 0; lu < posLim; lu++) {
            for (int b = 0; b < nbBloc; b++) {
                for (int p = 0; p < pileLim; p++) {
                    if (solution.getIntVal(printOrderedPos[compt].containers[i]) == 0) {
                        System.out.printf("  %s%d", "-", 0);
                    } else {
                        System.out.printf("%4d", solution.getIntVal(printOrderedPos[compt].containers[i]));
                    }
                    compt++;
                }
                System.out.printf(" %s", "|");
            }
            System.out.print("\n");
        }
        return compt;
    }

    private void printSeparator(String horizontalSeparator, String verticalSeparator){
        for (int b = 0; b < nbBloc; b++) {
            for (int p = 0; p < nbPileAbove; p++) {
                System.out.print(horizontalSeparator);
            }
            System.out.print(verticalSeparator);
        }
        System.out.print("\n");
    }

    private Position[] getPrintOrderedPos(){
        Position[] pos = new Position[positions.size()];
        int indexCompt = 0;
        int posCompteur;
        for (int b = 0; b < nbBay; b++) {
            for (int l = nbPosAbove + nbPosUnder - 1; l >= 0; l--) {
                posCompteur = b * nbBloc * (nbPileAbove * nbPosAbove + nbPileUnder * nbPosUnder) + l;
                for (int p = 0; p < nbPileAbove * nbBloc; p++) {
                    pos[indexCompt] = positions.get(posCompteur);
                    posCompteur += nbPosAbove + nbPosUnder;
                    indexCompt++;
                }
            }
        }
        return pos;
    }
}
