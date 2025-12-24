package class8.taxi_service;

import class4.race.Driver;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

// ------------------------------------------------------
// Domain classes
// ------------------------------------------------------

class RideRequest {
    private final String id;
    private final double x;
    private final double y;

    public RideRequest(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Rider{" + id + " @(" + x + "," + y + ")}";
    }
}

class DriverAvailableRequest {
    private final String id;
    private final double x;
    private final double y;

    public DriverAvailableRequest(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Driver{" + id + " @(" + x + "," + y + ")}";
    }
}

class Match {
    private final RideRequest rideRequest;
    private final DriverAvailableRequest driverAvailableRequest;
    private final double distance;
    private final Instant matchedAt = Instant.now();

    public Match(RideRequest r, DriverAvailableRequest d, double distance) {
        this.rideRequest = r;
        this.driverAvailableRequest = d;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Match: " + rideRequest.getId() + " -> " + driverAvailableRequest.getId() +
                " (dist=" + String.format("%.2f", distance) + ") at " + matchedAt;
    }
}

// ------------------------------------------------------
// Advanced ride-sharing service
// ------------------------------------------------------

class RideSharingService {

    private final BlockingQueue<RideRequest> rideRequestQueue = new LinkedBlockingQueue<>();

    private final List<DriverAvailableRequest> driverAvailableRequestPool = new ArrayList<>();
    private final ReentrantLock driverLock = new ReentrantLock(); // must lock for accessing pool

    private final BlockingQueue<Match> matches = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> rejections = new LinkedBlockingQueue<>();

    private final AtomicInteger matchCount = new AtomicInteger();
    private final AtomicInteger rejectionCount = new AtomicInteger();
    private final AtomicLong totalDistance = new AtomicLong();

    private final Random random = new Random();

    public void addRider(RideRequest r) {
        rideRequestQueue.offer(r);
        System.out.println("[RIDER] " + r);
    }

    public void addDriver(DriverAvailableRequest d) throws InterruptedException {
        driverLock.lock();
        try {
            driverAvailableRequestPool.add(d);
            System.out.println("[DRIVER] " + d);
        } finally {
            driverLock.unlock();
        }
    }


    // Dispatcher logic
    public void startDispatcher(String dispatcherName) {
        Thread dispatcher = new Thread(() -> {

            System.out.println("[DISPATCHER " + dispatcherName + "] Started");

            try {
                while (true) {

                    RideRequest request = rideRequestQueue.poll(1, TimeUnit.SECONDS);
                    boolean acquired = driverLock.tryLock(1, TimeUnit.SECONDS);
                    System.out.println(driverLock.isHeldByCurrentThread());
                    if (acquired) {

//                            Thread.sleep(200 + driverAvailableRequestPool.size() * 20L);

                        Optional<DriverAvailableRequest> searchResult = driverAvailableRequestPool.stream()
                                .min((left, right) -> Double.compare(distance(request, left), distance(request, right)));

                        if (searchResult.isPresent()) {
                            DriverAvailableRequest driver = searchResult.get();

                            Match match = new Match(request, driver, distance(request, driver));
                            driverAvailableRequestPool.remove(driver);

                            matches.offer(match);
                            matchCount.incrementAndGet();
                            totalDistance.addAndGet((long) distance(request, driver));

                            System.out.println("[MATCH] by dispatcher " + dispatcherName + " -> " + match);


                        } else { //empty drivers list
                            System.out.println(dispatcherName + "[REJECTION] No drivers available");
                            rejections.offer(dispatcherName + "[REJECTION] No drivers available");
                            rejectionCount.incrementAndGet();
                        }

                        driverLock.unlock();


                    } else {
                        System.out.println(dispatcherName + "[REJECTION] Cannot acquire lock after waiting some time");
                        rejections.offer(dispatcherName + "[REJECTION] Cannot acquire lock after waiting some time");

                        rejectionCount.incrementAndGet();
                    }
                }

            } catch (Exception e) {
                System.out.println("[DISPATCHER " + dispatcherName + "] Stopped");
            }

        }, "Dispatcher-" + dispatcherName);

        dispatcher.start();
    }

    private double distance(RideRequest r, DriverAvailableRequest d) {
        double dx = r.getX() - d.getX();
        double dy = r.getY() - d.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Metrics
    public int getMatchCount() {
        return matchCount.get();
    }

    public int getRejectionCount() {
        return rejectionCount.get();
    }

    public int getDriverCount() throws InterruptedException {
        if (driverLock.tryLock(3, TimeUnit.SECONDS)) {
            try {
                return driverAvailableRequestPool.size();
            } finally {
                driverLock.unlock();
            }
        }
        return -1;
    }

    public int getRiderQueueSize() {
        return rideRequestQueue.size();
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

public class RideSharingDemo {

    public static void main(String[] args) throws InterruptedException {
        RideSharingService service = new RideSharingService();
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
                    service.addRider(new RideRequest(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(random.nextInt(300));
                }
            } catch (InterruptedException ignored) {
            }
        };

        Runnable driverProducer = () -> {
            int c = 0;
            try {
                while (true) {
                    String id = "D" + Thread.currentThread().getId() + "-" + (c++);
                    service.addDriver(new DriverAvailableRequest(id, random.nextDouble() * 10, random.nextDouble() * 10));
                    Thread.sleep(250 + random.nextInt(350));
                }
            } catch (InterruptedException ignored) {
            }
        };

        producers.submit(riderProducer);
        producers.submit(riderProducer);
        producers.submit(driverProducer);
        producers.submit(driverProducer);

        // Metrics monitor
        ScheduledExecutorService monitor = Executors.newScheduledThreadPool(1);
        monitor.scheduleAtFixedRate(() -> {
            try {
                System.out.println(String.format(
                        "\n===== METRICS =====\n" +
                                "Waiting riders: %d\n" +
                                "Available drivers: %d\n" +
                                "Total matches: %d\n" +
                                "Rejections: %d\n" +
                                "Average distance: %.2f",
                        service.getRiderQueueSize(),
                        service.getDriverCount(),
                        service.getMatchCount(),
                        service.getRejectionCount(),
                        service.getAvgDistance()
                ));
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

        }, 2, 3, TimeUnit.SECONDS);

        Thread.sleep(60000);

        System.out.println("\n[MAIN] Stopping simulation...");
        producers.shutdownNow();
//        monitor.shutdownNow();
        System.out.println("[MAIN] Done.");
    }
}
