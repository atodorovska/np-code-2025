package class8.parallel_letter_counter;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

class ChunkProcessor extends Thread {
    String text;
    int start, end;
    Map<Character, Integer> result = new HashMap<>();

    public ChunkProcessor(String text, int start, int end) {
        this.text = text;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        result = text.substring(start, end).chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isLetter)
                .map(Character::toLowerCase)
                .collect(Collectors.groupingBy(
                        c -> c,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }
}


public class LetterFrequencyCounter {

    // ============================================
    // Sequential letter counter
    // ============================================
    public static Map<Character, Integer> countSequential(String text) {
        return text.chars()
                .mapToObj(i -> (char) i)
                .filter(Character::isLetter)
                .map(Character::toLowerCase)
                .collect(Collectors.groupingBy(
                        c -> c,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
    }


    // ============================================
    // Parallel letter counter
    // ============================================
    public static Map<Character, Integer> countParallel(String text, int numThreads) throws InterruptedException {
        int textSize = text.length();
        int chunkSize = (int) Math.ceil((float) textSize / numThreads);

        List<ChunkProcessor> processors = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(textSize, (i + 1) * chunkSize);
            processors.add(new ChunkProcessor(text, start, end));
        }

        processors.forEach(ChunkProcessor::start);

        for (ChunkProcessor processor : processors) {
            processor.join();
        }


        Map<Character, Integer> identity = new HashMap<>();
        for (char i = 'a'; i <= 'z'; i++) {
            identity.put(i, 0);
        }

        return processors.stream()
                .map(p -> p.result)
                .reduce(
                        identity,
                        (left, right) -> {
                            right.forEach((k, v) -> left.merge(k, v, Integer::sum));
                            return left;
                        }
                );


    }

    // ============================================
    // Helper → print histogram
    // ============================================
    public static void printCounts(Map<Character, Integer> map) {
        map.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        });
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
        Map<Character, Integer> map = countSequential(text);
        long endSeq = System.currentTimeMillis();

        System.out.println("Sequential time: " + (endSeq - startSeq) + " ms");
        System.out.println(map);

        // ====================================
        // Parallel timing
        // ====================================
        int numThreads = Runtime.getRuntime().availableProcessors();

//        System.out.println(numThreads);

        long startPar = System.currentTimeMillis();
        Map<Character, Integer> map2 = countParallel(text, numThreads);
        long endPar = System.currentTimeMillis();


        System.out.println("Parallel time (" + numThreads + " threads): " + (endPar - startPar) + " ms");
        System.out.println(map2);

        // ====================================
        // Compare results
        // ====================================
//        boolean same = true;
//        for (int i = 0; i < 26; i++) {
//            if (seqCounts[i] != parCounts[i]) {
//                same = false;
//                break;
//            }
//        }

//        if (same)
//            System.out.println("CORRECT: Sequential and parallel results match.");
//        else
//            System.out.println("ERROR: Results do NOT match!");

        System.out.println("---------------------------------------");
        System.out.println("Speedup: " + String.format("%.2f",
                (double) (endSeq - startSeq) / (endPar - startPar)) + "x");
    }
}