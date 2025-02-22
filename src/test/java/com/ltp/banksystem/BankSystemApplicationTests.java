package com.ltp.banksystem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//@SpringBootTest
class BankSystemApplicationTests {
    final Calculator calculator = new Calculator();

    @Test
    void itShouldAddTwoNumbers() {
        final int numberOne = 3;
        final int numberTwo = 5;

        final int expected = 8;
        final int actual = calculator.add(numberOne,numberTwo);

        assertThat(actual).isEqualTo(expected);
    }

    static class Calculator {
        public int add(final int numberOne, final int numberTwo) {
            return numberOne + numberTwo;
        }
    }
}
