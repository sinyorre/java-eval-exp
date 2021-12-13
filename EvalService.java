package com.example.demo;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by salagoz on Dec, 2021
 */
@Service
public class EvalService {
    static final Pattern numbersPattern = Pattern.compile("[0-9]+");
    static final Pattern opsPattern = Pattern.compile("[+-\\/*]+");
    static final Pattern parenthesisPattern = Pattern.compile("\\(\\s*[0-9]+(\\s*[+-\\/*]\\s*[0-9]+)+\\s*\\)");
    static final Pattern validCharPattern = Pattern.compile("[0-9+-\\/*()\\s]*");

    public double eval(String text) {
        validateInputText(text);
        String newText = evalParenthesis(text);
        return evalSimultaneous(newText);
    }

    private String evalParenthesis(String text) {
        String newText = text;
        String[] matchingStrings = getMatchingStrings(parenthesisPattern, newText);
        while (matchingStrings.length > 0) {
            for (String matchingString : matchingStrings) {
                int result = evalSimultaneous(matchingString);
                newText = newText.replace(matchingString, result + "");
            }
            matchingStrings = getMatchingStrings(parenthesisPattern, newText);
        }
        return newText;
    }

    private int evalSimultaneous(String text) {
        String[] numbers = getMatchingStrings(numbersPattern, text);
        String[] ops = getMatchingStrings(opsPattern, text);

        validateParsedVariables(numbers, ops);

        while (ops.length > 1) {
            int index = findFirstOpIndex(ops);
            String newText = doOperation(numbers, ops, index);
            numbers = getMatchingStrings(numbersPattern, newText);
            ops = getMatchingStrings(opsPattern, newText);
        }

        if (ops.length == 0) return Integer.parseInt(text.trim());
        return evalSimple(ops[0], numbers[0], numbers[1]);
    }

    private String doOperation(String[] numbers, String[] ops, int opIndex) {
        String op = ops[opIndex];
        String number1 = numbers[opIndex];
        String number2 = numbers[opIndex + 1];
        int result = evalSimple(op, number1, number2);
        return generateNewInputString(numbers, ops, opIndex, result);
    }

    private int findFirstOpIndex(String[] ops) {
        int index = -1;
        int maxValue = -1;
        for (int i = 0; i < ops.length; i++) {
            int value = ops[i].equals("*") || ops[i].equals("/") ? 2 : 1;
            if (value > maxValue) {
                maxValue = value;
                index = i;
            }
        }
        return index;
    }

    private String generateNewInputString(String[] numbers, String[] ops, int opIndex, int result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ops.length; i++) {
            if (i == opIndex) {
                stringBuilder.append(result);
                if (opIndex + 1 >= ops.length) return stringBuilder.toString();
                stringBuilder.append(ops[opIndex + 1]);
                i += 1;
                continue;
            }
            stringBuilder.append(numbers[i]);
            stringBuilder.append(ops[i]);
        }

        stringBuilder.append(numbers[numbers.length - 1]);

        return stringBuilder.toString();
    }

    private int evalSimple(String op, String number1, String number2) {
        int result = 0;
        switch (op) {
            case "+":
                result = Integer.parseInt(number1) + Integer.parseInt(number2);
                break;
            case "-":
                result = Integer.parseInt(number1) - Integer.parseInt(number2);
                break;
            case "*":
                result = Integer.parseInt(number1) * Integer.parseInt(number2);
                break;
            case "/":
                result = Integer.parseInt(number1) / Integer.parseInt(number2);
                break;
        }
        return result;
    }

    private String[] getMatchingStrings(Pattern pattern, String text) {
        List<String> allMatches = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            allMatches.add(matcher.group());
        }
        return allMatches.toArray(new String[0]);
    }

    private void validateInputText(String text) {
        if (!validCharPattern.matcher(text).matches()) throw new RuntimeException("Invalid Char");
    }

    private void validateParsedVariables(String[] numbers, String[] ops) {
        boolean duplicateOperationCheck = Arrays.stream(ops).anyMatch(s -> s.length() > 1);
        if (numbers.length != ops.length + 1) throw new RuntimeException("Syntax Error");
        if (duplicateOperationCheck) throw new RuntimeException("Duplicate Operations");
    }
}
