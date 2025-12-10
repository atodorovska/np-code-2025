package class8.parallel_api_calls;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {

    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

    public static String extractContent(String json) {

        // Find the `"content":` field AFTER `"message"`
        String messageMarker = "\"message\":";
        int messageIndex = json.indexOf(messageMarker);
        if (messageIndex == -1) return null;

        String contentMarker = "\"content\":";
        int contentIndex = json.indexOf(contentMarker, messageIndex);
        if (contentIndex == -1) return null;

        // Find first quote AFTER "content":
        int startQuote = json.indexOf("\"", contentIndex + contentMarker.length());
        if (startQuote == -1) return null;

        // Find the closing quote
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;

        // Extract content
        String result = json.substring(startQuote + 1, endQuote);

        // Handle escaped newlines or quotes if needed
        return result.replace("\\n", "\n").replace("\\\"", "\"");
    }


    // ================================================================
    // MAKE A SINGLE OPENAI REQUEST
    // ================================================================
    public static String callOpenAI(String prompt) throws IOException {
        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = """
                {
                  "model": "gpt-4o-mini",
                  "messages": [
                    { "role": "system", "content": "You are a helpful assistant that answers questions about multithreading concepts in Java. Please use always max one paragraph." },
                    { "role": "user", "content": "%s" }
                  ]
                }
                """.formatted(prompt.replace("\"", "'"));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        if (conn.getResponseCode() == 200) {
            return extractContent(new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
        } else {
            return new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    // ================================================================
    // SEQUENTIAL EXECUTION
    // ================================================================
    public static List<String> runSequential(List<String> prompts) throws IOException {
        return prompts.stream()
                .map(p -> {
                    try {
                        return callOpenAI(p);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // ================================================================
    // CONCURRENT EXECUTION
    // ================================================================
    public static List<String> runConcurrent(List<String> prompts, int threadCount) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(threadCount, prompts.size()));
        List<Future<String>> futures = new ArrayList<>();
        for (String prompt : prompts) {
            futures.add(executor.submit(() -> callOpenAI(prompt)));
        }

        List<String> responses = new ArrayList<>();
        for (Future<String> future : futures) {
            responses.add(future.get(5, TimeUnit.SECONDS));
        }

        executor.shutdown();


        return responses;
    }

    // ================================================================
    // MAIN
    // ================================================================
    public static void main(String[] args) throws Exception {

        // Load all text files from /questions directory
        Path promptDir = Paths.get("src/class8/questions");
        if (!Files.exists(promptDir)) {
            System.err.println("Directory 'questions/' not found!");
            return;
        }

        List<String> prompts = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(promptDir, "*.txt")) {
            for (Path p : stream) {
                prompts.add(Files.readString(p));
            }
        }

        System.out.println("Loaded " + prompts.size() + " prompts.");
        System.out.println("======================================");

        // Sequential
        long startS = System.currentTimeMillis();
        List<String> sequentialResults = runSequential(prompts);
        long endS = System.currentTimeMillis();
        System.out.println("Sequential time: " + (endS - startS) + " ms");

        System.out.println("Sequential results: " + sequentialResults.stream().collect(Collectors.joining("\n")));

        // Concurrent (using CPU cores)
        int threads = Math.min(prompts.size(), Runtime.getRuntime().availableProcessors());
        long startC = System.currentTimeMillis();
        List<String> concurrentResults = runConcurrent(prompts, threads);
        long endC = System.currentTimeMillis();
        System.out.println("Concurrent time (" + threads + " threads): " + (endC - startC) + " ms");

        System.out.println("Concurrent results: " + concurrentResults.stream().collect(Collectors.joining("\n")));

        System.out.println("======================================");
        System.out.println("Speedup: compare times above manually.");
    }
}