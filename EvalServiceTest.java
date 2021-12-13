package com.example.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

class EvalServiceTest {

    @Test
    void testEvalSimple() {
        EvalService evalService = new EvalService();
        Assertions.assertEquals(8, evalService.eval("3 + 5"));
        Assertions.assertEquals(-2, evalService.eval("3 - 5"));
        Assertions.assertEquals(15, evalService.eval("3 * 5"));
        Assertions.assertEquals(2, evalService.eval("10 / 5"));
    }

    @Test
    void testEvalSimultaneous() {
        EvalService evalService = new EvalService();
        Assertions.assertEquals(73, evalService.eval("3 + 5 * 7 * 2"));
    }

    @Test
    void testEvalParenthesis() {
        EvalService evalService = new EvalService();
        Assertions.assertEquals(77, evalService.eval("(5 +               ((3  +   5) + (3 + 10 * 5 / 2 ) ) * 2)   "));
        Assertions.assertEquals(268, evalService.eval("1 + 2 * 3 / 2 + (3+5) * (33)"));
    }

    @Test
    void testEvalThrowsSyntaxError() {
        EvalService evalService = new EvalService();
        Assertions.assertThrows(RuntimeException.class, () -> evalService.eval("1 + 2 * 3 / 2 + (3+5) -> 3"), "Syntax Error");
    }

    @Test
    void testEvalThrowsDuplicateOperationsError() {
        EvalService evalService = new EvalService();
        Assertions.assertThrows(RuntimeException.class, () -> evalService.eval("1 ++ 2"), "Duplicate Operations");
    }

    @Test
    void testEvalThrowsInvalidCharError() {
        EvalService evalService = new EvalService();
        Assertions.assertThrows(RuntimeException.class, () -> evalService.eval("1 -> 2"), "Invalid Char");
    }
}