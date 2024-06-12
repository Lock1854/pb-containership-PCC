package org.containershipPb;

import java.util.Random;

public class Generator {
    int[][] planification;
    public Generator(Data data) {
        planification = new int[data.nbCont][2];
        Random random = new Random();

        for (int i = 0; i < data.nbCont; i++) {
            int[] pair = generateValidPair(random, data);
            planification[i] = pair;
        }
    }

    // Method to generate a valid pair of integers where the first is strictly less than the second
    private int[] generateValidPair(Random random, Data data) {
        int first, second;
        do {
            first = random.nextInt(data.nbStop);
            second = random.nextInt(data.nbStop);
        } while (first >= second);
        return new int[]{first, second};
    }
}
