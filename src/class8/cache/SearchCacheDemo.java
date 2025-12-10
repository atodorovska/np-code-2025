package class8.cache;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

// ============================================================================
// SearchResult: stores results + TTL + locking
// ============================================================================
class SearchResult {
    private List<String> results;
    private Instant expiresAt;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SearchResult(List<String> results, long ttlSeconds) {
        this.results = results;
        this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void update(List<String> newResults, long ttlSeconds) {
        lock.writeLock().lock();
        try {
            this.results = newResults;
            this.expiresAt = Instant.now().plusSeconds(ttlSeconds);
            System.out.println(String.format("Updated results: %s with expiry at %d", this.results, this.expiresAt.toEpochMilli()));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> getResults() {
        lock.readLock().lock();
        try {
            return List.copyOf(results);
        } finally {
            lock.readLock().unlock();
        }
    }

    public ReadWriteLock getLock() {
        return lock;
    }
}

// ============================================================================
// SearchEngine: simulates slow search API
// ============================================================================
class SearchEngine {

    public List<String> search(String query) {
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}

        return List.of(
                query + " - result A",
                query + " - result B",
                query + " - result C " + new Random().nextInt(100)
        );
    }
}

// ============================================================================
// SearchCache: handles cache lookup, TTL, fallback search, concurrency
// ============================================================================
class SearchCache {

    private final ConcurrentHashMap<String, SearchResult> cache = new ConcurrentHashMap<>();
    private final SearchEngine searchEngine;
    private final long ttlSeconds;

    public SearchCache(SearchEngine searchEngine, long ttlSeconds) {
        this.searchEngine = searchEngine;
        this.ttlSeconds = ttlSeconds;
    }

    public List<String> search(String query) {
        SearchResult entry = cache.get(query);

        // Fast path: cached & not expired
        if (entry != null && !entry.isExpired()) {
            return entry.getResults();
        }

        // compute() prevents race conditions and ensures only one fetch
        return cache.compute(query, (q, existing) -> {

            if (existing != null && !existing.isExpired()) {
                return existing;
            }

            List<String> fresh = searchEngine.search(query);

            if (existing == null) {
                return new SearchResult(fresh, ttlSeconds);
            } else {
                System.out.println(String.format("Starting to update results for search term %s with %s", q, fresh));
                existing.update(fresh, ttlSeconds);
                return existing;
            }

        }).getResults();
    }

    public ConcurrentHashMap<String, SearchResult> getCacheMap() {
        return cache;
    }
}

// ============================================================================
// SearchCacheCleaner: scheduled cleanup of expired entries
// ============================================================================
class SearchCacheCleaner {

    private final SearchCache cache;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public SearchCacheCleaner(SearchCache cache) {
        this.cache = cache;
    }

    public void startCleanupTask() {
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[Cleanup] Checking expired entries...");

            for (Map.Entry<String, SearchResult> entry : cache.getCacheMap().entrySet()) {
                SearchResult sr = entry.getValue();

                sr.getLock().readLock().lock();
                try {
                    if (sr.isExpired()) {
                        System.out.println(" -> Removing: " + entry.getKey());
                        cache.getCacheMap().remove(entry.getKey());
                    }
                } finally {
                    sr.getLock().readLock().unlock();
                }
            }

        }, 3, 3, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }
}

// ============================================================================
// Main: handles user input & demonstrates caching behavior
// ============================================================================
public class SearchCacheDemo {

    public static void main(String[] args) throws InterruptedException {
        SearchEngine engine = new SearchEngine();
        SearchCache cache = new SearchCache(engine, 5); // TTL = 10 seconds
        SearchCacheCleaner cleaner = new SearchCacheCleaner(cache);
        cleaner.startCleanupTask();

//        Scanner sc = new Scanner(System.in);
//        System.out.println("=== Search Cache Demo (TTL = 10s) ===");

        //sequential searches
//        while (true) {
//            System.out.print("\nSearch query (type 'exit' to quit): ");
//            String q = sc.nextLine().trim();
//
//            if (q.equalsIgnoreCase("exit")) break;
//
//            long start = System.currentTimeMillis();
//            List<String> results = cache.search(q);
//            long end = System.currentTimeMillis();
//
//            System.out.println("Results: " + results);
//            System.out.println("Time: " + (end - start) + " ms");
//            System.out.println("(Cache size: " + cache.getCacheMap().size() + ")");
//        }

        //concurrent searches

        System.out.println("=== Concurrent Search Cache Demo (TTL = 10s) ===");

        // Example queries different threads will use
        List<String> queries = List.of(
                "java", "concurrency", "executor", "cache", "ttl"
//                "java", "concurrency", "executor", "cache", "ttl"
        );

        ExecutorService executor = Executors.newFixedThreadPool(5);
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            String query = i%1000!=0 ? queries.get(random.nextInt(queries.size())) : "Gordana";
            if (i%10==0) {
                Thread.sleep(random.nextInt(2000, 4000));
            }
            executor.submit(() -> {
                try {
                    long start = System.currentTimeMillis();
                    List<String> results = cache.search(query);
                    long end = System.currentTimeMillis();

                    System.out.println(Thread.currentThread().getName()
                            + " | Query: " + query
                            + " | Results: " + results
                            + " | Time: " + (end - start) + " ms");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("\n=== All threads finished ===");
        System.out.println("Final cache size: " + cache.getCacheMap().size());

        cleaner.stop();
    }
}