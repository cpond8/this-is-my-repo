package Calculator;
// NAME:  Christopher Pond
// ID #:  201925869
// MAIL:  christopher.pond@bellevuecollege.edu
// DATE:  2024 May 30

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class Validator {
    private final HashSet<Character> operators = new HashSet<>();
    private final HashSet<String> functions = new HashSet<>();
    private final HashSet<String> constants = new HashSet<>();
    private final HashMap<Character, Character> closingPairs = new HashMap<>();
    private final HashMap<Character, Character> openingPairs = new HashMap<>();
    private final HashMap<String, String> errors = new HashMap<>();
    private String expression;

    Validator() {
        loadOperators();
        loadFunctions();
        loadConstants();
        loadPairs();
        loadErrors();
    }

    public boolean isValid(String expression) {
        Stack<Character> stack = new Stack<>();
        Stack<Integer> lastOpenInterval = new Stack<>();
        this.expression = expression;

        for (int i = 0; i < expression.length(); i++) {
            char c = at(i);

            if (Character.isDigit(c) || c == '.') {
                String nextNumber = saveNextNumberFrom(i);
                if (!nextNumber.isEmpty()) { // invalid decimal points will be caught later as invalid char
                    i += nextNumber.length() - 1;
                    continue;
                }
            }

            if (Character.isLetter(c)) {
                String nextWord = saveNextWordFrom(i);

                if (!functions.contains(nextWord) && !constants.contains(nextWord)) {
                    if (nextWord.equals("E")) {
                        printErrorMessage("E", i, "INVALID_EXPONENT");
                        return false;
                    }
                    printErrorMessage(nextWord, i, "INVALID_FUNCTION");
                    return false;
                }
                i += nextWord.length() - 1;

                if (functions.contains(nextWord)
                        && (isFinalCharAt(i) || !isOpeningBracket(after(i)))
                ) {
                    printErrorMessage(nextWord, i + 1, "NO_FUNCTION_BRACKETS");
                    return false;
                }
                continue;
            }

            if (c == '-' && isValidNegativeSignAt(i)) {
                continue;
            }

            if (isOperator(c)) {
                if (!isValidOperatorAt(i)) {
                    printErrorMessage(c, i, "INVALID_OPERATOR");
                    return false;
                }
                continue;
            }

            if (isOpeningBracket(c)) {
                stack.push(c);
                lastOpenInterval.push(i);
                continue;
            }

            if (isClosingBracket(c)) {
                if (stack.isEmpty()) {
                    printErrorMessage(matchingBracketOf(c), i, "UNPAIRED_BRACKET");
                    return false;
                }
                char pairedBracket = stack.pop();

                if (matchingBracketOf(c) != pairedBracket) {
                    printErrorMessage(matchingBracketOf(pairedBracket), i, "MISMATCHED_BRACKET");
                    return false;
                }
                if (isOpeningBracket(before(i))) {
                    printErrorMessage(
                            String.format("%c%c", pairedBracket, c),
                            i - 1,
                            "EMPTY_BRACKETS"
                    );
                    return false;
                }
                lastOpenInterval.pop();
                continue;
            }

            printErrorMessage(c, i, "INVALID_CHAR");
            return false;
        }

        if (!stack.isEmpty()) {
            printErrorMessage(
                    matchingBracketOf(stack.pop()),
                    lastOpenInterval.pop(),
                    "UNPAIRED_BRACKET"
            );
            return false;
        }
        return true;
    }

    <T> void printErrorMessage(T s, int index, String errorCode) {
        int EXPRESSION_OFFSET = 9;
        for (int i = 0; i < (index + EXPRESSION_OFFSET); i++) {
            System.out.print(" ");
        }
        System.out.printf(
                "^ [ERROR] %s at char %d\n",
                String.format(errors.get(errorCode), s),
                index + 1
        );
    }

    private boolean isValidNegativeSignAt(int i) {
        if (isFinalCharAt(i) || (i > 0 && isOperator(before(i)) && isOperator(after(i)))) {
            return false;
        }
        return isDigitOrLetter(after(i))
                || isOperator(after(i))
                || isOpeningBracket(after(i))
                || after(i) == '.';
    }

    private boolean isValidOperatorAt(int i) {
        if (i == 0 || isFinalCharAt(i)) {
            return false;
        }
        if (!isDigitOrLetter(before(i)) && !isClosingBracket(before(i))) {
            return false;
        }
        return isDigitOrLetter(after(i))
                || isOpeningBracket(after(i))
                || after(i) == '.'
                || after(i) == '-'
                || isOperator(after(i)); // invalid, but error will be caught by the following operator
    }

    private boolean isFinalCharAt(int i) {
        return i + 1 >= expression.length();
    }

    private String saveNextNumberFrom(int i) {
        StringBuilder nextNumber = new StringBuilder();
        boolean hasDecimal = false;
        boolean hasExponent = false;

        if (i > 0 && at(i) == '.' && Character.isDigit(before(i))) {
            return "";
        }

        for (int j = i;
             j < expression.length() && isNumberChar(at(j));
             j++
        ) {
            if (at(j) == '.') {
                if (hasDecimal) {
                    break;
                }
                hasDecimal = true;
            }
            if (at(j) == 'E') {
                if (hasExponent) {
                    break;
                }
                if (!isFinalCharAt(j) && after(j) == '-') {
                    nextNumber.append(at(j));
                    j++;
                }
                hasExponent = true;
            }
            nextNumber.append(at(j));
        }
        char lastChar = nextNumber.charAt(nextNumber.length() - 1);

        if (lastChar == '.' || lastChar == 'E') { // remove last char if decimal or E (error will be caught later)
            nextNumber.setLength(nextNumber.length() - 1);
        }
        return nextNumber.toString();
    }

    private String saveNextWordFrom(int i) {
        StringBuilder nextWord = new StringBuilder();

        for (int j = i;
             j < expression.length() && Character.isLetter(at(j));
             j++
        ) {
            nextWord.append(at(j));
        }
        return nextWord.toString();
    }

    private char before(int i) {
        return expression.charAt(i - 1);
    }

    private char after(int i) {
        return expression.charAt(i + 1);
    }

    private char at(int i) {
        return expression.charAt(i);
    }

    private char matchingBracketOf(char c) {
        return isOpeningBracket(c) ? openingPairs.get(c) : closingPairs.get(c);
    }

    private boolean isNumberChar(char c) {
        return Character.isDigit(c) || c == '.' || c == 'E';
    }

    private boolean isDigitOrLetter(char c) {
        return (Character.isDigit(c) || Character.isLetter(c))
                && c != 'E'; // exponent E is not counted as a letter
    }

    private boolean isOperator(char c) {
        return operators.contains(c)
               || functions.contains(Character.toString(c)); // functions count as operators for this algorithm
    }

    private boolean isOpeningBracket(char c) {
        return openingPairs.containsKey(c);
    }

    private boolean isClosingBracket(char c) {
        return closingPairs.containsKey(c);
    }

    private void loadOperators() {
        operators.add('+');
        operators.add('*');
        operators.add('-');
        operators.add('/');
        operators.add('^');
    }

    private void loadFunctions() {
        functions.add("sqrt");
        functions.add("cbrt");
        functions.add("ln");
        functions.add("deg");
        functions.add("log");
        functions.add("sin");
        functions.add("cos");
        functions.add("tan");
        functions.add("arcsin");
        functions.add("arccos");
        functions.add("arctan");
        functions.add("abs");
    }

    private void loadConstants() {
        constants.add("pi");
        constants.add("e");
    }

    private void loadPairs() {
        closingPairs.put(')','(');
        closingPairs.put('}','{');
        closingPairs.put(']','[');
        openingPairs.put('(',')');
        openingPairs.put('{','}');
        openingPairs.put('[',']');
    }

    private void loadErrors() {
        errors.put("INVALID_CHAR", "invalid character: '%s'"); // 1@2
        errors.put("UNPAIRED_BRACKET", "unpaired bracket: '%s' needed for bracket"); // 1(2((3+4))+5
        errors.put("MISMATCHED_BRACKET", "mismatched bracket: '%s' expected"); // 1(2+3]+4
        errors.put("INVALID_OPERATOR", "invalid operator: '%s'"); // 1+-2(-pi+3//4)
//        errors.put("INVALID_DECIMAL", "invalid decimal"); // 1.0+2.3+4.0pi+5.pi+6.7 (merged with INVALID_CHAR)
        errors.put("INVALID_EXPONENT", "invalid exponent: \"%s\""); // 6.67E-11-1.23E5+e+9.87E+pi
        errors.put("INVALID_FUNCTION", "invalid function: \"%s\""); // arctan(-tan(5pi/4))+sine(pi/2)
        errors.put("NO_FUNCTION_BRACKETS", "missing brackets: argument of \"%s\" must be wrapped in brackets starting"); // 1+2sin3pi/4
        errors.put("EMPTY_BRACKETS", "empty brackets: \"%s\" starting"); // 1+2(-.0)()+3
    }
}
