package org.containershipPb;

import java.util.Random;

public class Generator {
    int[][] planification;
    public Generator(int nbCont, int nbStop) {
        planification = new int[nbCont][2];
        Random random = new Random();

        for (int c = 0; c < nbCont; c++) {
            int[] pair = generateValidPair(random, nbStop);
            planification[c] = pair;
        }
    }

    // Method to generate a valid pair of integers where the first is strictly less than the second
    private int[] generateValidPair(Random random, int nbStop) {
        int first, second;
        do {
            first = random.nextInt(nbStop);
            second = random.nextInt(nbStop);
        } while (first >= second);
        return new int[]{first, second};
    }
}
