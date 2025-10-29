package class1.calculator;

public class AdditionStrategy implements CalculateStrategy {

    @Override
    public double calculate(double a, double b) {
        return a + b;
    }
}
