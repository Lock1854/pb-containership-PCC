package org.containershipPb;

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

import static org.containershipPb.Data.containers;
import static org.containershipPb.PbSolver.*;
import static org.containershipPb.Ship.*;

public class CSP {
    IntVar[][] move;
    IntVar[] restow;
    IntVar restowTot;
    Boolean restowAllowed, table;
    TupleGenerator tupleGen;
    ArrayList<IntVar> allIntVar = new ArrayList<>();
    Boolean testSymmetry = true;

    public CSP(Boolean restowAllowed, Boolean table){
        this.restowAllowed = restowAllowed;
        this.table = table;
        initialiseContVar();
        if (testSymmetry) initialisePosVar();
        move = model.intVarMatrix("move", ship.nbPosPan, nbStop, 0, 2, false);
        allIntVar.addAll(getAllMove());
        nbVar += ship.nbPosPan * nbStop;
        if (restowAllowed) {
            restow = model.intVarArray("restow", nbStop, 0, 5, false);
            allIntVar.addAll(Arrays.stream(restow).toList());
            restowTot = model.intVar("restowTot", 0, nbStop * ship.nbPosPan, false);
            allIntVar.add(restowTot);
            nbVar += nbStop + 1;
        }
        tupleGen = new TupleGenerator();
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
                if (!restowAllowed && i < nbStop - 1 && !pos.isHatch) forbidRestow(pos, i);
                if (testSymmetry && !pos.isHatch) {
                    for (Container cont : containers) {
                        if (i > cont.load && i <= cont.unload) {
                            makePosContEquiv(cont, pos, i);
                        }
                    }
                }
            }
            if (testSymmetry) {
                for (Type type : data.types) {
                    if (i == type.load + 1) {
                        for (int c = 0; c < type.containers.size() - 1; c++) {
                            breakSymmetryInStack(type.containers.get(c), type.containers.get(c + 1), i);
                        }
                    }
                }
                if (nbPileUnder > 1 || nbPileAbove > 1) {
                    for (ArrayList<Position> symPos : symPosStack) {
                        if (i > 0) breakBigSymmetry(symPos, i);
                    }
                }
                if (nbBloc > 1){
                    for (ArrayList<Position> symPos : symPosBloc) {
                        if (i > 0) breakBigSymmetry(symPos, i);
                    }
                }
                if (nbBay > 1){
                    for (ArrayList<Position> symPos : symPosBay) {
                        if (i > 0) breakBigSymmetry(symPos, i);
                    }
                }
            }
        }
    }

    private void makePosContEquiv(Container cont, Position pos, int i){
        if (table) model.table(pos.containers[i], cont.positions[i], tupleGen.getContPosEquivTuples(cont, pos, i)).post();
        else{
            model.ifOnlyIf(
                    model.arithm(cont.positions[i], "=", pos.number),
                    model.arithm(pos.containers[i], "=", cont.number)
            );
        }
    }

    private void forceDifferentPositions(int i){
        model.allDifferent(getAllContent(i)).post();
    }

    private void ensureAllTransported(int i){
        IntVar[] t = model.intVarArray(data.onboardConts.get(i).size(), new int[]{1});
        model.globalCardinality(getAllContent(i), data.onboardContsNo(i), t, false).post();
    }

    private void ensureStack(Position pos, int i){
        if (pos.support != null && !pos.support.isHatch) {
            if (table){
                model.table(pos.containers[i], pos.support.containers[i], tupleGen.getStackTuples(pos)).post();
            } else {
                model.ifThen(
                        model.arithm(pos.containers[i], "!=", - pos.number),
                        model.arithm(pos.support.containers[i], "!=", - pos.support.number)
                );
            }
        }
    }

    private void computeMove(Position pos, int i){
        if (table && !pos.isHatch) {
            if (i == nbStop - 1) {
                model.table(
                        new IntVar[]{pos.containers[i], move[pos.number][i]},
                        tupleGen.getMovePosTuples(pos, true)
                ).post();
            } else if (pos.support == null){
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], model.intVar(0)},
                        tupleGen.getMovePosTuples(pos, false)
                ).post();
            } else {
                model.table(
                        new IntVar[]{pos.containers[i], pos.containers[i + 1], move[pos.number][i], move[pos.support.number][i]},
                        tupleGen.getMovePosTuples(pos, false)
                ).post();
            }
            if (!pos.isHatch && pos.isUnder) {
                model.table(
                        new IntVar[]{move[pos.number][i], move[pos.pile.bloc.hatch.number][i]},
                        tupleGen.getMovePanTuples()
                ).post();
            }
        }
        else if (!pos.isHatch){
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
            if (pos.isUnder) {
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
        if (table) model.table(new IntVar[]{pos.containers[i], pos.containers[i+1], move[pos.number][i]}, tupleGen.getNoRestowTuples(pos, i)).post();
        else {
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
            model.ifThen(
                    model.arithm(pos.containers[i], "=", pos.containers[i+1]),
                    model.arithm(move[pos.number][i], "=", 0)
            );
        }
    }

    private void breakSymmetryInStack(Container cont1, Container cont2, int i){
        model.arithm(cont1.positions[i], "<", cont2.positions[i]).post();
    }

    private void breakBigSymmetry(ArrayList<Position> symPos, int i){
        for (int j = 1; j < symPos.size(); j++) {
            Position pos = symPos.get(j);
            Position previousPos = symPos.get(j-1);
            if (table) model.table(new IntVar[]{pos.containers[i], pos.containers[i-1], previousPos.containers[i]}, tupleGen.getBlocSymmetryTuples(pos, previousPos)).post();
            else {
                model.ifThen(
                        model.and(
                                model.arithm(pos.containers[i], "!=", -pos.number),
                                model.arithm(pos.containers[i - 1], "=", -pos.number)
                        ),
                        model.arithm(previousPos.containers[i], "!=", -previousPos.number)
                );
            }
        }
    }

    private void initialiseContVar(){
        for (Position pos : positions){
            if (pos.isHatch){
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", new int[]{- pos.number});
                    nbVar++;
                    allIntVar.add(pos.containers[i]);
                }
            }
            else {
                for (int i = 0; i < nbStop; i++) {
                    pos.containers[i] = model.intVar("cont[" + pos.number + "][" + i + "]", data.onboardContsNo(i, pos.number));
                    nbVar++;
                    allIntVar.add(pos.containers[i]);
                }
            }
        }
    }

    private void initialisePosVar(){
        for (Container cont : containers){
            for (int i = 0; i < nbStop; i++) {
                if (i > cont.load && i <= cont.unload) {
                    cont.positions[i] = model.intVar("position[" + cont.number + "][" + i + "]", 0, ship.nbPos - 1);
                    nbVar++;
                } else cont.positions[i] = null;
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
            ArrayList<Position> printOrderedPos = getPrintOrderedPos();
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

    private int printPiles(Solution solution, ArrayList<Position> printOrderedPos, int i, int compteur, Boolean isAbove) {
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
                    if ( i == nbStop - 1 || solution.getIntVal(printOrderedPos.get(compt).containers[i+1]) <= 0) {
                        System.out.printf("%4s", ".");
                    } else {
                        System.out.printf("%4d", containers.get(solution.getIntVal(printOrderedPos.get(compt).containers[i+1])-1).type.num);
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

    private ArrayList<Position> getPrintOrderedPos(){
        ArrayList<Position> pos = new ArrayList<>(positions.size());
        for (int b = 0; b < nbBay; b++) {
            pos.addAll(getHalfBayOrderedPos(b, false));
            pos.addAll(getHalfBayOrderedPos(b, true));
        }
        return pos;
    }

    static int posCompteur;
    private ArrayList<Position> getHalfBayOrderedPos(int b, Boolean under){
        ArrayList<Position> pos = new ArrayList<>(positions.size());
        int limPos;
        int limPile;
        if (under) {
            limPos = nbPosUnder;
            limPile = nbPileUnder;
        }
        else {
            limPos = nbPosAbove;
            limPile = nbPileAbove;
        }
        for (int l = limPos - 1; l >= 0; l--) {
            posCompteur = b * nbBloc * (nbPileAbove * nbPosAbove + nbPileUnder * nbPosUnder) + l;
            if (!under) posCompteur += (nbPileUnder * nbPosUnder);
            for (int bl = 0; bl < nbBloc; bl++) {
                for (int p = 0; p < limPile; p++) {
                    pos.add(positions.get(posCompteur));
                    posCompteur += limPos;
                }
                if (!under) posCompteur += nbPileUnder * nbPosUnder;
                else posCompteur += nbPileAbove * nbPosAbove;
            }
        }
        return pos;
    }
}
