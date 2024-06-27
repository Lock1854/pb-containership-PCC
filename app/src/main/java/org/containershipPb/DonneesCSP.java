package org.containershipPb;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class DonneesCSP {
    public static void main(String[] args) {
        Path path = Paths.get("donnees-csp-opti-poscont.csv");
        List<String[]> doc;
        try (Reader reader = Files.newBufferedReader(path)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                doc = csvReader.readAll();
            }
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }

        PbSolver.nbBay = 2;
        PbSolver.nbBloc = 2;
        PbSolver.nbPileAbove = 2;
        PbSolver.nbPosAbove = 2;

        for (String[] line : doc) {
            if (Objects.equals(line[0], "NbCont")) continue;
            for (int i = 0; i < 10; i++) {
                PbSolver.nbCont = Integer.parseInt(line[0]);
                if (Objects.equals(line[1], "16")) {
                    PbSolver.nbPileUnder = 0;
                } else if (Objects.equals(line[1], "32")) {
                    PbSolver.nbPileUnder = 2;
                }
                PbSolver ps = new PbSolver();
                line[2+i] = String.valueOf(ps.nbVarSup);
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(path.toString()))) {
            writer.writeAll(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
