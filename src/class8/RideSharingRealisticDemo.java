package class8;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

// ------------------------------------------------------
// Domain classes
// ------------------------------------------------------

class Rider {
    private final String id;
    private final double x;
    private final double y;

    public Rider(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() {
        return "Rider{" + id + " @(" + x + "," + y + ")}";
    }
}

class Driver {
    private final String id;
    private final double x;
    private final double y;

    public Driver(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }

    @Override
    public String toString() {
        return "Driver{" + id + " @(" + x + "," + y + ")}";
    }
}

class Match {
    private final Rider rider;
    private final Driver driver;
    private final double distance;
    private final Instant matchedAt = Instant.now();

    public Match(Rider r, Driver d, double distance) {
        this.rider = r;
        this.driver = d;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Match: " + rider.getId() + " -> " + driver.getId() +
                " (dist=" + String.format("%.2f", distance) + ") at " + matchedAt;
    }
}

// ------------------------------------------------------
// Advanced ride-sharing service
// ------------------------------------------------------

class RideSharingRealisticService {

    private final BlockingQueue<Rider> riderQueue = new LinkedBlockingQueue<>();

    private final List<Driver> driverPool = new ArrayList<>();
    private final ReentrantLock driverLock = new ReentrantLock(); // must lock for accessing pool

    private final BlockingQueue<Match> matches = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> rejections = new LinkedBlockingQueue<>();

    private final AtomicInteger matchCount = new AtomicInteger();
    private final AtomicInteger rejectionCount = new AtomicInteger();
    private final AtomicLong totalDistance = new AtomicLong();

    private final Random random = new Random();

    public void addRider(Rider r) {
        riderQueue.offer(r);
        System.out.println("[RIDER] " + r);
    }

    public void addDriver(Driver d) {
        driverLock.lock();
        try {
            driverPool.add(d);
        } finally {
            driverLock.unlock();
        }
        System.out.println("[DRIVER] " + d);
    }

    // Dispatcher logic
    public void startDispatcher(String dispatcherName) {
        Thread dispatcher = new Thread(() -> {

            System.out.println("[DISPATCHER " + dispatcherName + "] Started");

            try {
                while (true) {

                    Rider r = riderQueue.take();

                    // Try to access drivers within 2 seconds
                    boolean gotLock = driverLock.tryLock(2, TimeUnit.SECONDS);

                    if (!gotLock) {
                        rejectionCount.incrementAndGet();
                        String reason = "[REJECT] " + dispatcherName +
                                " => Could not access drivers in time for " + r.getId();
                        rejections.offer(reason);
                        System.out.println(reason);

                        continue;
                    }

                    Driver closest = null;
                    double bestDist = Double.MAX_VALUE;

                    try {
                        if (driverPool.isEmpty()) {
                            rejectionCount.incrementAndGet();
                            String reason = "[REJECT] " + dispatcherName +
                                    " => No drivers available for " + r.getId();
                            rejections.offer(reason);
                            System.out.println(reason);

                            continue;
                        }

                        // artificial delay for searching (simulate database or GPS lookup)
                        Thread.sleep(200 + random.nextInt(300));

                        for (Driver d : driverPool) {
                            double dist = distance(r, d);
                            try { Thread.sleep(20); } catch (InterruptedException ignored) {} // simulate slow search
                            if (dist < bestDist) {
                                bestDist = dist;
                                closest = d;
                            }
                        }

                        driverPool.remove(closest);

                    } finally {
                        driverLock.unlock();
                    }

                    Match m = new Match(r, closest, bestDist);
                    matches.offer(m);

                    totalDistance.addAndGet((long) bestDist);
                    matchCount.incrementAndGet();

                    System.out.println("[MATCH by " + dispatcherName + "] " + m);
                }

            } catch (InterruptedException e) {
                System.out.println("[DISPATCHER " + dispatcherName + "] Stopped");
            }

        }, "Dispatcher-" + dispatcherName);

        dispatcher.start();
    }

    private double distance(Rider r, Driver d) {
        double dx = r.getX() - d.getX();
        double dy = r.getY() - d.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Metrics
    public int getMatchCount() { return matchCount.get(); }
    public int getRejectionCount() { return rejectionCount.get(); }

    public int getDriverCount() {
        driverLock.lock();
        try {
            return driverPool.size();
        } finally {
            driverLock.unlock();
        }
    }

    public int getRiderQueueSize() {
        return riderQueue.size();
    }

    public double getAvgDistance() {
        int count = matchCount.get();
        if (count == 0) return 0;
        return totalDistance.get() * 1.0 / count;
    }
}

// ------------------------------------------------------
// Demo
// ------------------------------------------------------

public class RideSharingRealisticDemo {

    public static void main(String[] args) throws InterruptedException {
        RideSharingRealisticService service = new RideSharingRealisticService();
        Random random = new Random();

        // Start multiple dispatchers
        service.startDispatcher("Alice");
        service.startDispatcher("Bob");
        service.startDispatcher("Charlie");

        ExecutorService producers = Executors.newFixedThreadPool(4);

        Runnable riderProducer = () -> {
            int c = 0;
            try {
                while (true) {
                    String id = "R" + Thread.currentThread().getId() + "-" + (c++);
                    service.addRider(new Rider(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(random.nextInt(300));
                }
            } catch (InterruptedException ignored) {}
        };

        Runnable driverProducer = () -> {
            int c = 0;
            try {
                while (true) {
                    String id = "D" + Thread.currentThread().getId() + "-" + (c++);
                    service.addDriver(new Driver(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(250 + random.nextInt(350));
                }
            } catch (InterruptedException ignored) {}
        };

        producers.submit(riderProducer);
        producers.submit(riderProducer);
        producers.submit(driverProducer);
        producers.submit(driverProducer);

        // Metrics monitor
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            System.out.println("\n===== METRICS =====");
            System.out.println("Waiting riders: " + service.getRiderQueueSize());
            System.out.println("Available drivers: " + service.getDriverCount());
            System.out.println("Total matches: " + service.getMatchCount());
            System.out.println("Rejections: " + service.getRejectionCount());
            System.out.println("Average distance: " + String.format("%.2f", service.getAvgDistance()));
        }, 2, 3, TimeUnit.SECONDS);

        Thread.sleep(60000);

        System.out.println("\n[MAIN] Stopping simulation...");
        producers.shutdownNow();
        monitor.shutdownNow();
        System.out.println("[MAIN] Done.");
    }
}
