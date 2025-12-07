package class8;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.*;

public class SystemHealthChecker {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            Runtime rt = Runtime.getRuntime();

            long totalMem = rt.totalMemory();
            long freeMem = rt.freeMemory();
            long usedMem = totalMem - freeMem;

            System.out.println("---- Health ----");
            System.out.println("Available processors: " + rt.availableProcessors());
            System.out.println("Used memory (MB): " + (usedMem / (1024 * 1024)));
            System.out.println("Total memory (MB): " + (totalMem / (1024 * 1024)));
            System.out.println("System load avg: " + osBean.getSystemLoadAverage());
            System.out.println("----------------");
        };

        // Run every 2 seconds
        scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);

        // Let it run for 30 seconds then shut down
        Thread.sleep(30_000);
        System.out.println("Shutting down...");
        scheduler.shutdown();
        scheduler.awaitTermination(5, TimeUnit.SECONDS);
    }
}
