package class1.calculator;

import java.util.Scanner;

public class Calculator {

    private double state;

    public Calculator() {
        this.state = 0;
    }

    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        Scanner input = new Scanner(System.in);

        while (true) {
            String line = input.nextLine();

            if (line.toLowerCase().startsWith("n")) {
                System.out.println("Final Result: " + calculator.state);
                break;
            } else if (line.toLowerCase().startsWith("y")) {
                calculator.state = 0;
                System.out.println("Result: " + calculator.state);
                continue;
            } else if (line.toLowerCase().startsWith("r")) {
                System.out.println("Final Result: " + calculator.state);
                continue;
            } else if (!line.toLowerCase().matches("[+\\-*/]\\s*\\d+")) {
                System.out.println("Invalid Input");
                continue;
            }

            String[] tokens = line.split("\\s+");
            char operation = tokens[0].charAt(0);
            double amount = Double.parseDouble(tokens[1]);

            CalculateStrategy strategy = null;

            if (operation == '+') {
                strategy = new AdditionStrategy();
            } else if (operation == '-') {
                strategy = new SubstractionStrategy();
            } else if (operation == '*') {
                strategy = new MultiplicationStrategy();
            } else if (operation == '/') {
                strategy = new DivisionStrategy();
            } else {
                System.out.println("Invalid operation!");
            }
            calculator.state = strategy.calculate(calculator.state, amount);
            System.out.println("Result: " + calculator.state);
        }
    }
}
