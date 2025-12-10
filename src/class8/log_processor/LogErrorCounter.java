package class8.log_processor;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class ErrorCounterThread extends Thread {
    Path path;
    long result;
    public ErrorCounterThread(Path path) {
        this.path = path;
    }
    @Override
    public void run() {
        try {
            result = Files.lines(path)
                    .filter(line -> line.contains("ERROR"))
                    .count();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public long getResult() {
        return result;
    }
}


public class LogErrorCounter {

    // ================================
    // Sequential approach
    // ================================
    public static long countErrorsSequential(List<Path> files) {
        return files.stream()
                .flatMap(path -> {
                    try {
                        return Files.lines(path);
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        return Stream.empty();
                    }
                })
                .filter(line -> line.contains("ERROR"))
                .count();
    }

    // ================================
    // Concurrent approach
    // ================================


    public static long countErrorsConcurrent(List<Path> files) throws InterruptedException {
        List<ErrorCounterThread> threads = files.stream()
                .map(ErrorCounterThread::new)
                .collect(Collectors.toList());

        threads.forEach(ErrorCounterThread::start);

        for (ErrorCounterThread thread : threads) {
            thread.join();
        }

        return threads.stream()
                .mapToLong(ErrorCounterThread::getResult)
                .sum();

    }

    // ================================
    // Runner with time measurement
    // ================================
    public static void main(String[] args) throws Exception {

        Path logsDir = Paths.get("src/class8/logs"); // <-- adjust if needed

        if (!Files.isDirectory(logsDir)) {
            System.err.println("Directory 'logs' not found!");
            return;
        }

        // Collect all .log files
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(logsDir, "*.log")) {
            for (Path p : ds) {
                files.add(p);
//                files.add(p);
            }
        }

        if (files.isEmpty()) {
            System.out.println("No log files found.");
            return;
        }

        System.out.println("Found " + files.size() + " log files.");
        System.out.println("-------------------------------------------");

        // Sequential measurement
        long startSeq = System.currentTimeMillis();
        long seqTotal = countErrorsSequential(files);
        long endSeq = System.currentTimeMillis();

        System.out.println("Sequential total errors: " + seqTotal);
        System.out.println("Sequential time: " + (endSeq - startSeq) + " ms");
        System.out.println();

        // Concurrent measurement
        long startConc = System.currentTimeMillis();
        long concTotal = countErrorsConcurrent(files);
        long endConc = System.currentTimeMillis();

        System.out.println("Concurrent total errors: " + concTotal);
        System.out.println("Concurrent time: " + (endConc - startConc) + " ms");
        System.out.println();

        // Cross-check correctness
        if (seqTotal == concTotal) {
            System.out.println("CORRECT: Both methods produced the same result.");
        } else {
            System.out.println("WARNING: Results differ! Sequential=" +
                    seqTotal + ", Concurrent=" + concTotal);
        }

        System.out.println("-------------------------------------------");
        System.out.println("Speedup: " + String.format("%.2f",
                (double) (endSeq - startSeq) / (endConc - startConc)) + "x faster");
    }
}