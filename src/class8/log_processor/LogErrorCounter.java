package class8.log_processor;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class LogErrorCounter {

    // ================================
    // Sequential approach
    // ================================
    public static long countErrorsSequential(List<Path> files) {
        long total = 0;
        for (Path file : files) {
            try {
                long count = Files.lines(file)
                        .filter(line -> line.contains("ERROR"))
                        .count();
                total += count;
            } catch (IOException e) {
                System.err.println("Failed to read file: " + file);
            }
        }
        return total;
    }

    // ================================
    // Concurrent approach
    // ================================
    private static class ErrorCounterTask extends Thread {
        private final Path file;
        private long errorCount = 0;

        public ErrorCounterTask(Path file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                errorCount = Files.lines(file)
                        .filter(line -> line.contains("ERROR"))
                        .count();
            } catch (IOException e) {
                System.err.println("Failed to read file: " + file);
            }
        }

        public long getErrorCount() {
            return errorCount;
        }
    }

    public static long countErrorsConcurrent(List<Path> files) throws InterruptedException {
        List<ErrorCounterTask> tasks = new ArrayList<>();

        // Create and start threads
        for (Path file : files) {
            ErrorCounterTask task = new ErrorCounterTask(file);
            tasks.add(task);
            task.start();
        }

        // Wait for all threads
        long total = 0;
        for (ErrorCounterTask task : tasks) {
            task.join();
            total += task.getErrorCount();
        }
        return total;
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
            for (Path p : ds) files.add(p);
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
