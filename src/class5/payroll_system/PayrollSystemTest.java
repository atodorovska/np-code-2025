package class5.payroll_system;

import java.util.*;

public class PayrollSystemTest {

    public static void main(String[] args) {
        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployeesData(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, emps) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            emps.forEach(System.out::println);
            System.out.println("------------");
        });

        System.out.println("TOTAL PAY PER EMPLOYEE:");
        payrollSystem.totalPayPerEmployee().entrySet().stream()
                     .sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
                     .forEach(e -> System.out.printf("%s -> %.2f%n", e.getKey(), e.getValue()));
    }
}

