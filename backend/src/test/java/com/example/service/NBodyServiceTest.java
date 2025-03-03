package com.example.service;


import com.example.service.NBodyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NBodyServiceTest {
    private NBodyService service;

    @BeforeEach
    void setUp() {
        service = new NBodyService();
    }

    @Test
    void testInitialParticleCount() {
        assertEquals(5000, service.getParticles().size());
    }
}