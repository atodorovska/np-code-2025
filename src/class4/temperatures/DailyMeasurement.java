package class4.temperatures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DailyMeasurement implements Comparable<DailyMeasurement> {

    private final int day;
    private List<Double> temperatures;

    public DailyMeasurement(int day, List<Double> temperatures) {
        this.day = day;
        this.temperatures = temperatures;
    }

    public static double convertFtoC(double temperature) {
        return (temperature - 32) * 5 / 9;
    }

    public static double convertCtoF(double temperature) {
        return (temperature * 9) / 5 + 32;
    }

    public static DailyMeasurement createMeasurement(String line) {
        String[] parts = line.split("\\s+");
        int day = Integer.parseInt(parts[0]);
        List<Double> temperatures = new ArrayList<>();

        String splitBy = "C";
        if (parts[1].contains("F"))
            splitBy = "F";

        for (int i = 1; i < parts.length; i++) {
            String[] tempParts = parts[i].split(splitBy);
            if (splitBy.equals("F"))
                temperatures.add(convertFtoC(Double.parseDouble(tempParts[0])));
            else
                temperatures.add(Double.parseDouble(tempParts[0]));
        }
        return new DailyMeasurement(day, temperatures);
    }

    public static String getMeasurementStats(DailyMeasurement dailyMeasurement, char scale) {
        if (scale == 'F')
            dailyMeasurement.temperatures = dailyMeasurement.temperatures.stream()
                                                                         .map(DailyMeasurement::convertCtoF)
                                                                         .collect(Collectors.toList());

        int count = dailyMeasurement.temperatures.size();
        double min = dailyMeasurement.temperatures.stream().min(Double::compare).orElse(0.0);
        double max = dailyMeasurement.temperatures.stream().max(Double::compare).orElse(0.0);
        double sum = dailyMeasurement.temperatures.stream().mapToDouble(Double::doubleValue).sum();
        double average = sum / count;

        return String.format(
                "%3d Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                dailyMeasurement.day, count, min, scale, max, scale, average, scale
        );
    }

    @Override
    public int compareTo(DailyMeasurement o) {
        return this.day - o.day;
    }
}
