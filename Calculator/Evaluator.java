package Calculator;
// NAME:  Christopher Pond
// ID #:  201925869
// MAIL:  christopher.pond@bellevuecollege.edu
// DATE:  2024 May 30

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Evaluator {
    private final HashMap<String, Double> constants = new HashMap<>();

    Evaluator() {
        loadConstants();
    }

    public String evaluate(String expression) {
        Scanner input = new Scanner(expression);
        Stack<Double> stack = new Stack<>();

        while (input.hasNext()) {
            if (input.hasNextDouble()) {
                stack.push(input.nextDouble());
                continue;
            }
            String s = input.next();

            if (constants.containsKey(s)) {
                stack.push(constants.get(s));
                continue;
            }
            double b = stack.pop();
            double a = Character.isLetter(s.charAt(0))
                        ? -1
                        : stack.pop();
            stack.push(operate(a, b, s));
        }

        return formatForDisplay(stack.pop());
    }

    private double operate(double a, double b, String operator) {
        if (operator.equals("/") && b == 0) {
            System.out.println(" [ERROR] division by zero");
            return Double.NaN;
        }
        return switch (operator) {
            case "*" -> a * b;
            case "+" -> a + b;
            case "/" -> a / b;
            case "-" -> a - b;
            case "^" -> Math.pow(a, b);
            case "sqrt" -> Math.sqrt(b);
            case "cbrt" -> Math.cbrt(b);
            case "ln" -> Math.log(b);
            case "log" -> Math.log10(b); // other programs (such as java) might treat "log" as ln
            case "deg" -> Math.toRadians(b);
            case "sin" -> Math.sin(b);
            case "cos" -> Math.cos(b);
            case "tan" -> Math.tan(b);
            case "arcsin" -> Math.asin(b);
            case "arccos" -> Math.acos(b);
            case "arctan" -> Math.atan(b);
            case "abs" -> Math.abs(b);
            default -> -1;
        };
    }

    private String formatForDisplay(double result) {
        String template = "#,###.#########"; // TI-84 output lacks commas, but I think they look nice
        if ((result < 1 && result > 0) || isExtremeNumber(result)) {
            template = isExtremeNumber(result) // recreates TI-84 output precision
                        ? "0.#########E0"
                        : "0.##########";
        }
        DecimalFormat decimal = new DecimalFormat(template);
        return decimal.format(result);
    }

    private boolean isExtremeNumber(double result) {
        return Math.abs(result) <= 1E-4 || Math.abs(result) >= 1E10;
    }

    private void loadConstants() {
        constants.put("π", Math.PI);
        constants.put("e", Math.E);
        constants.put("g", 9.80665);
        constants.put("G", 6.67E-11);
    }
}
