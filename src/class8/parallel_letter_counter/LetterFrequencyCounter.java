package class8.parallel_letter_counter;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class LetterFrequencyCounter {

    // ============================================
    // Sequential letter counter
    // ============================================
    public static int[] countSequential(String text) {
        int[] counts = new int[26];

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int index = Character.toLowerCase(c) - 'a';
                if (index >= 0 && index < 26) {
                    counts[index]++;
                }
            }
        }

        return counts;
    }

    // ============================================
    // Worker thread for parallel processing
    // ============================================
    private static class LetterCounterWorker extends Thread {
        private final String text; //shared memory
        private final int start;
        private final int end;
        private final int[] localCounts = new int[26]; //local memory

        public LetterCounterWorker(String text, int start, int end) {
            this.text = text;
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                char c = text.charAt(i);
                if (Character.isLetter(c)) {
                    int index = Character.toLowerCase(c) - 'a';
                    if (index >= 0 && index < 26) {
                        localCounts[index]++;
                    }
                }
            }
        }

        public int[] getLocalCounts() {
            return localCounts;
        }
    }

    // ============================================
    // Parallel letter counter
    // ============================================
    public static int[] countParallel(String text, int numThreads) throws InterruptedException {
        int length = text.length();
        int chunk = length / numThreads;

        List<LetterCounterWorker> workers = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunk;
            int end = (i == numThreads - 1) ? length : start + chunk;

            LetterCounterWorker worker = new LetterCounterWorker(text, start, end);
            workers.add(worker);
            worker.start();
        }

        // Combine results
        int[] globalCounts = new int[26];

        for (LetterCounterWorker w : workers) {
            w.join();
            int[] local = w.getLocalCounts();
            for (int i = 0; i < 26; i++) {
                globalCounts[i] += local[i];
            }
        }

        return globalCounts;
    }

    // ============================================
    // Helper → print histogram
    // ============================================
    public static void printCounts(int[] counts) {
        for (int i = 0; i < 26; i++) {
            char letter = (char) ('a' + i);
            System.out.printf("%c: %d%n", letter, counts[i]);
        }
    }

    // ============================================
    // MAIN — loads text, runs sequential & parallel
    // ============================================
    public static void main(String[] args) throws Exception {

        Path filePath = Paths.get("src/class8/large_text.txt");

        if (!Files.exists(filePath)) {
            System.err.println("File 'large_text.txt' not found!");
            return;
        }

        String text = Files.readString(filePath);

        System.out.println("Text length: " + text.length());
        System.out.println("---------------------------------------");

        // ====================================
        // Sequential timing
        // ====================================
        long startSeq = System.currentTimeMillis();
        int[] seqCounts = countSequential(text);
        long endSeq = System.currentTimeMillis();

        System.out.println("Sequential time: " + (endSeq - startSeq) + " ms");

        // ====================================
        // Parallel timing
        // ====================================
        int numThreads = 2;
//                Runtime.getRuntime().availableProcessors();

//        System.out.println(numThreads);

        long startPar = System.currentTimeMillis();
        int[] parCounts = countParallel(text, 4);
        long endPar = System.currentTimeMillis();

        System.out.println("Parallel time (" + numThreads + " threads): " + (endPar - startPar) + " ms");

        // ====================================
        // Compare results
        // ====================================
        boolean same = true;
        for (int i = 0; i < 26; i++) {
            if (seqCounts[i] != parCounts[i]) {
                same = false;
                break;
            }
        }

        if (same)
            System.out.println("CORRECT: Sequential and parallel results match.");
        else
            System.out.println("ERROR: Results do NOT match!");

        System.out.println("---------------------------------------");
        System.out.println("Speedup: " + String.format("%.2f",
                (double) (endSeq - startSeq) / (endPar - startPar)) + "x");
    }
}
