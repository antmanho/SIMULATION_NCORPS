package com.example.model;

import com.example.model.Particle;
import com.example.model.Vector2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ParticleTest {
    @Test
    void testParticleInitialization() {
        Particle p = new Particle(1, new Vector2(1.0f, 2.0f), new Vector2(0.1f, 0.2f), 5.0f);
        assertEquals(1, p.index);
        assertEquals(1.0f, p.position.x);
        assertEquals(2.0f, p.position.y);
    }
}
