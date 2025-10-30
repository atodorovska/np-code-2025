package class2;

import java.util.Scanner;

interface MathOperation {
    double perform (double a, double b);
}

class UnknownOperatorException extends Exception {
    public UnknownOperatorException(char operator) {
        super(String.format("%c is unknown operation", operator));
    }
}

class MathOperationFactory {
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';

    private static final MathOperation ADDITION = (a,b) -> a+b;
    private static final MathOperation SUBTRACTION = (a,b) -> a-b;

    private static final MathOperation MULTIPLICATION = (a,b) -> a*b;

    private static final MathOperation DIVISION = (a,b) -> a/b;

    public static MathOperation get (char operator) throws UnknownOperatorException {
        if (operator == PLUS) {
            return ADDITION;
        } else if (operator == MINUS) {
            return SUBTRACTION;
        } else if (operator == MULTIPLY) {
            return MULTIPLICATION;
        } else if (operator == DIVIDE) {
            return DIVISION;
        } else {
            throw new UnknownOperatorException(operator);
        }
    }



}

class Calculator {
    private double result;


    public Calculator() {
        result = 0;
    }

    public String init() {
        return String.format("result = %f", result);
    }

    public double getResult() {
        return result;
    }

    public String execute(char operator, double value) throws UnknownOperatorException {
        MathOperation operation = MathOperationFactory.get(operator);
        result = operation.perform(result, value);
        return String.format("result %c %f = %f", operator, value, result);
    }



    @Override
    public String toString() {
        return String.format("updated result = %f", result);
    }

}

public class CalculatorTest {
    static final char RESULT = 'r';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Calculator calculator = new Calculator();
            System.out.println(calculator.init());
            while (true) {
                String line = scanner.nextLine();
                char choice = getCharLower(line);
                if (choice == RESULT) {
                    System.out.println(String.format("final result = %f", calculator.getResult()));
                    break;
                }
                String[] parts = line.split("\\s+");
                char operator = parts[0].charAt(0);
                double value = Double.parseDouble(parts[1]);
                try {
                    String result = calculator.execute(operator, value);
                    System.out.println(result);
                    System.out.println(calculator);
                } catch (UnknownOperatorException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.println("(Y/N)");
            String line = scanner.nextLine();
            char choice = getCharLower(line);
            if (choice == 'n') {
                break;
            }
        }
    }

    static char getCharLower(String line) {
        if (line.trim().length() > 0) {
            return Character.toLowerCase(line.charAt(0));
        }
        return '?';
    }
}