package Calculator;
// NAME:  Christopher Pond
// ID #:  201925869
// MAIL:  christopher.pond@bellevuecollege.edu
// DATE:  2024 May 30

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Postfixer {
    private final HashMap<Character, Integer> operators = new HashMap<>();
    private final HashMap<String, Character> functionCodes = new HashMap<>();
    private final HashMap<Character, String> functionNames = new HashMap<>();
    private final HashMap<String, Character> constants = new HashMap<>();
    private final HashMap<Character, Character> pairs = new HashMap<>();
    Stack<Character> stack = new Stack<>();
    private final StringBuilder infix = new StringBuilder();
    private final StringBuilder postfix = new StringBuilder();

    Postfixer() {
        loadOperators();
        loadFunctions();
        loadConstants();
        loadPairs();
    }

    public String postfix(String input) {
        loadInfix(input);
        postfix.setLength(0); // clear postfix from previous uses

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            if (Character.isDigit(c) || c == '.' || c == 'E') {
                postfix.append(c);
                continue;
            }

            if (isConstant(c)) {
                handleJuxtapositionBefore(i);
                postfix.append(c);
                if (i + 1 < infix.length() && isNumber(after(i)))
                    pushOperator('$');
                continue;
            }

            if (isOpeningBracket(c)) {
                handleJuxtapositionBefore(i);
                stack.push(c);
                continue;
            }

            if (isClosingBracket(c)) {
                while (stack.peek() != pairs.get(c)) {
                    popStackThenAdd();
                }
                stack.pop();
                if (i + 1 < infix.length() && Character.isDigit(after(i)))
                    pushOperator('$');
                continue;
            }

            if (c == '-') {
                if (isNegativeNumberSignAt(i)) {
                    postfix.append(c);
                    continue;
                }
                if (isNegativeMultiplierAt(i)) {
                    postfix.append(-1);
                    pushOperator('$');
                    continue;
                }
            }

            if (c == '^' && !stack.isEmpty() && stack.peek() == '^') {
                pushOperator('&');
                continue;
            }

            if (isOperator(c)) {
                pushOperator(c);
                continue;
            }

            if (Character.isLetter(c)) {
                StringBuilder function = new StringBuilder();
                for (int j = i;
                     isLetterInAt(String.valueOf(infix), j);
                     j++
                ) {
                    function.append(infix.charAt(j));
                }
                handleJuxtapositionBefore(i);
                pushOperator(functionCodes.get(function.toString()));
                i += function.length() - 1;
                continue;
            }
            // validator should catch everything, so ideally this will never be called
            Validator validator = new Validator();
            validator.printErrorMessage(c, i, "INVALID_CHAR");
            return "";
        }

        while (!stack.isEmpty()) {
            popStackThenAdd();
        }
        return postfix.toString();
    }

    private void loadInfix(String input) {
        infix.setLength(0);
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetter(c)) {
                StringBuilder letters = new StringBuilder();
                for (int j = i; isLetterInAt(input, j); j++) {
                    letters.append(input.charAt(j));
                }
                infix.append(
                        constants.containsKey(letters.toString())
                        ? constants.get(letters.toString())
                        : letters
                );
                i += letters.length() - 1;
                continue;
            }
            infix.append(
                    c == '.' && (i == 0 || !Character.isDigit(infix.charAt(infix.length() - 1)))
                    ? "0."
                    : c
            );
        }
    }

    private void popStackThenAdd() {
        char symbol = stack.pop();
        postfix.append(
            Character.isLetter(symbol)
            ? String.format(" %s", functionNames.get(symbol))
            : String.format(" %c", symbol)
        );
    }

    private void pushOperator(char c) {
        while (!stack.isEmpty()
                && !isOpeningBracket(stack.peek())
                && precedenceOf(c) <= precedenceOf(stack.peek())
        ) {
            popStackThenAdd();
        }
        if (!Character.isLetter(c)) {
            postfix.append(' ');
        }
        c = c == '$' ? '*' : c; // implicit multiplication
        c = c == '&' ? '^' : c; // nested exponentiation
        stack.push(c);
    }

    private void handleJuxtapositionBefore(int i) {
        if (i > 0 && (isNumber(before(i)) || isClosingBracket(before(i)))) {
            pushOperator('$');
        }
    }

    private boolean isNegativeNumberSignAt(int i) {
        return Character.isDigit(after(i))
                && i != 0
                && !isConstant(before(i))
                && (isOperator(before(i))
                    || Character.isLetter(before(i))
                    || isOpeningBracket(before(i)));
    }

    private boolean isNegativeMultiplierAt(int i) {
        return (isNumber(after(i)) || isOpeningBracket(after(i)) || Character.isLetter(after(i)))
                && (i == 0 || isOperator(before(i)) || isOpeningBracket(before(i)));
    }

    private boolean isLetterInAt(String token, int i) {
        return i < token.length() && Character.isLetter(token.charAt(i));
    }

    private char before(int i) {
        return infix.charAt(i - 1);
    }

    private char after(int i) {
        return infix.charAt(i + 1);
    }

    private int precedenceOf(char c) {
        return isOpeningBracket(c)
                ? 0
                : operators.getOrDefault(c, 4); // functions are always precedence 4
    }

    private boolean isNumber(char c) {
        return Character.isDigit(c) || isConstant(c);
    }

    private boolean isConstant(char c) {
        return constants.containsValue(c);
    }

    private boolean isOpeningBracket(char c) {
        return pairs.containsValue(c);
    }

    private boolean isClosingBracket(char c) {
        return pairs.containsKey(c);
    }

    private boolean isOperator(char c) {
        return operators.containsKey(c);
    }

    private void loadOperators() {
        operators.put('+', 1);
        operators.put('-', 1);
        operators.put('*', 2);
        operators.put('/', 2);
        operators.put('^', 3);
        operators.put('$', 4); // implicit multiplication
        operators.put('&', 4); // nested exponentiation
    }

    private void loadFunctions() {
        functionCodes.put("sqrt",'q');
        functionCodes.put("cbrt",'b');
        functionCodes.put("ln",'n');  // natural log
        functionCodes.put("log",'l'); // log base 10
        functionCodes.put("deg",'d');
        functionCodes.put("sin",'s');
        functionCodes.put("cos",'c');
        functionCodes.put("tan",'t');
        functionCodes.put("arcsin",'x');
        functionCodes.put("arccos",'y');
        functionCodes.put("arctan",'z');
        functionCodes.put("abs",'a');

        for (Map.Entry<String, Character> entry : functionCodes.entrySet()) {
            functionNames.put(entry.getValue(), entry.getKey());
        }
    }

    private void loadConstants() {
        constants.put("pi", 'π');
        constants.put("e", 'e');
    }

    private void loadPairs() {
        pairs.put(')','(');
        pairs.put('}','{');
        pairs.put(']','[');
    }
}