package class5.payroll_system;

import java.util.List;

public class FreelanceEmployee extends Employee {

    List<Integer> ticketPoints;

    public FreelanceEmployee(String ID, String level, double rate, List<Integer> ticketPoints) {
        super(ID, level, rate);
        this.ticketPoints = ticketPoints;
    }

    @Override
    double calculateSalary() {
        return ticketPoints.stream().mapToInt(tp -> tp).sum() * rate;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(
                " Tickets count: %d Tickets points: %d",
                ticketPoints.size(),
                ticketPoints.stream().mapToInt(i -> i).sum()
        );
    }
}