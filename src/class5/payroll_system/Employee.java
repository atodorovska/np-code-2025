package class5.payroll_system;

import java.util.Comparator;

public abstract class Employee implements Comparable<Employee> {

    String ID;
    String level;
    double rate;

    public Employee(String ID, String level, double rate) {
        this.ID = ID;
        this.level = level;
        this.rate = rate;
    }

    abstract double calculateSalary();

    public String getLevel() {
        return level;
    }

    public String getID() {
        return ID;
    }

    @Override
    public int compareTo(Employee o) {
        return Comparator
                .comparing(Employee::calculateSalary, Comparator.reverseOrder())
                .thenComparing(Employee::getLevel)
                .compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", ID, level, calculateSalary());
    }
}
