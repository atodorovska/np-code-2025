package class5.payroll_system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmployeeFactory {

    public static Employee createEmployee(String line, Map<String, Double> hourlyRate, Map<String, Double> ticketRate) {
        String[] parts = line.split(";");
        String type = parts[0];
        String id = parts[1];
        String level = parts[2];

        if (type.equalsIgnoreCase("H")) { // HourlyEmployee
            double hours = Double.parseDouble(parts[3]);
            Double rate = hourlyRate.get(level);
            if (rate == null) {
                throw new IllegalArgumentException("Unknown hourly level: " + level);
            }
            return new HourlyEmployee(id, level, rate, hours);
        } else if (type.equalsIgnoreCase("F")) { // FreelanceEmployee
            Double rate = ticketRate.get(level);
            if (rate == null) {
                throw new IllegalArgumentException("Unknown freelance level: " + level);
            }

//            List<Integer> ticketPoints = new ArrayList<>();
//            for (int i = 3; i < parts.length; i++) {
//                ticketPoints.add(Integer.parseInt(parts[i]));
//            }

            List<Integer> ticketPoints = Arrays.stream(parts)
                                               .skip(3)
                                               .map(Integer::parseInt)
                                               .collect(Collectors.toList());
            return new FreelanceEmployee(id, level, rate, ticketPoints);
        } else {
            throw new IllegalArgumentException("Unknown employee type: " + type);
        }
    }
}