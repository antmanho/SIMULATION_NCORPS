package com.example.model;

import com.example.model.Vector2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Vector2Test {
    @Test
    void testVector2SetMethod() {
        Vector2 v = new Vector2(3.0f, 4.0f);
        v.set(5.0f, 6.0f);
        assertEquals(5.0f, v.x);
        assertEquals(6.0f, v.y);
    }
}