package class5.payroll_system;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PayrollSystem {
    Map<String, Double> hourlyRate;
    Map<String, Double> ticketRate;
    List<Employee> employees;

    public PayrollSystem(Map<String, Double> hourlyRate, Map<String, Double> ticketRate) {
        this.hourlyRate = hourlyRate;
        this.ticketRate = ticketRate;
        this.employees = new ArrayList<>();
    }

    void readEmployeesData(InputStream is) {
        this.employees = new BufferedReader(new InputStreamReader(is))
                .lines()
                .filter(l -> l != null && !l.trim().isEmpty())
                .map(line -> EmployeeFactory.createEmployee(line, hourlyRate, ticketRate))
                .collect(Collectors.toList());
    }

    Map<String, Set<Employee>> printEmployeesByLevels(OutputStream os, Set<String> levels) {
        Map<String, Set<Employee>> grouped = employees.stream()
                                                      .collect(Collectors.groupingBy(
                                                              Employee::getLevel,
                                                              (Supplier<TreeMap<String, Set<Employee>>>) TreeMap::new,
                                                              Collectors.toCollection(TreeSet::new)
                                                      ));

        Map<String, Set<Employee>> result = new HashMap<>();
        for (String lvl : levels) {
            Set<Employee> set = grouped.get(lvl);
            if (set != null) {
                result.put(lvl, set);
            }
        }
        return result;
    }

    Map<String, Double> totalPayPerEmployee() {
        Map<String, Double> totals = new HashMap<>();
        for (Employee e : employees) {
            if (totals.containsKey(e.getID())) {
                totals.putIfAbsent(e.getID(), e.calculateSalary());
            } else {
                double currentValue = totals.get(e.getID());
                currentValue += e.calculateSalary();
                totals.put(e.getID(), currentValue);
            }

//            employees.stream()
//                     .collect(Collectors.groupingBy(
//                             Employee::getLevel,
//                             TreeMap::new,
//                             Collectors.summingDouble(Employee::calculateSalary)
//                     ));


//            totals.merge(e.getID(), e.calculateSalary(), Double::sum);
        }
        return totals;
    }
}
