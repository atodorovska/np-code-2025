package class1.calculator;

public class DivisionStrategy implements CalculateStrategy {

    @Override
    public double calculate(double a, double b) {
        if (b != 0)
            return a / b;
        else
            return a;
    }
}
