package fr.insa.geofast;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LombokTestTest {

    @Test
    void getName() {
        LombokTest toto = new LombokTest();
        toto.setName("testName");
        assertEquals("testName", toto.getName());
    }

    @Test
    void getAddress() {
        LombokTest lt = new LombokTest();
        lt.setAddress("testAddress");
        assertEquals("testAddress", lt.getAddress());
    }

    @Test
    void setName() {
        var lt = new LombokTest("testName", "testAddress");
        lt.setName("testName");
        assertEquals("testName", lt.getName());
    }

    @Test
    void setAddress() {
        var lt = new LombokTest();
        lt.setAddress("testAddress");
        assertEquals("testAddress", lt.getAddress());
    }
}