package class5.payroll_system;

public class HourlyEmployee extends Employee {

    double hours;
    double overtime;
    double regular;

    public HourlyEmployee(String ID, String level, double rate, double hours) {
        super(ID, level, rate);
        this.hours = hours;
        this.overtime = Math.max(0, hours - 40);
        this.regular = hours - overtime;
    }

    @Override
    double calculateSalary() {
        return regular * rate + overtime * rate * 1.5;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Regular hours: %.2f Overtime hours: %.2f", regular, overtime);
    }
}
