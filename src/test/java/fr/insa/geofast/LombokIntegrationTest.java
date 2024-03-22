package fr.insa.geofast;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LombokIntegrationTest {

    @Test
    void getName() {
        LombokIntegration toto = new LombokIntegration();
        toto.setName("testName");
        assertEquals("testName", toto.getName());
    }

    @Test
    void getAddress() {
        LombokIntegration lt = new LombokIntegration();
        lt.setAddress("testAddress");
        assertEquals("testAddress", lt.getAddress());
    }

    @Test
    void setName() {
        var lt = new LombokIntegration("testName", "testAddress");
        lt.setName("testName");
        assertEquals("testName", lt.getName());
    }

    @Test
    void setAddress() {
        var lt = new LombokIntegration();
        lt.setAddress("testAddress");
        assertEquals("testAddress", lt.getAddress());
    }
}
