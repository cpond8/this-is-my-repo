package Calculator;
// NAME:  Christopher Pond
// ID #:  201925869
// MAIL:  christopher.pond@bellevuecollege.edu
// DATE:  2024 May 30

import java.util.Scanner;

public class Main {
     public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Validator validator = new Validator();
        Postfixer postfixer = new Postfixer();
        Evaluator evaluator = new Evaluator();

        while (true) {
            System.out.print("\nEnter a math expression: ");
            String cleanedExpression = cleanInput(scanner.nextLine());

            if (cleanedExpression.isEmpty()) {
                System.out.print("Bye!");
                break;
            }
            System.out.printf("  Infix: %s\n", cleanedExpression);
            if (!validator.isValid(cleanedExpression)) {
                continue;
            }
            String postfixedExpression = postfixer.postfix(cleanedExpression);
            if (postfixedExpression.isEmpty()) {
                continue;
            }
            System.out.printf("Postfix: %s\n", postfixedExpression);
            System.out.printf(" Answer: %s\n", evaluator.evaluate(postfixedExpression));
        }
    }

    private static String cleanInput(String input) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            output.append(c);
        }
        return output.toString();
    }
}
