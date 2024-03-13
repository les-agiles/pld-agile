package org.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class TestUtilTest {

    @Test
    void getGreeting() {
        assertEquals("Hello, world!", TestUtil.getGreeting());
    }

    @Test
    void add() {
        assertNotEquals(6, TestUtil.add(2, 3));
    }
}
