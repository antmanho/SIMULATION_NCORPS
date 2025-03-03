package com.example.model;

import com.example.model.QuadTree;
import com.example.model.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QuadTreeTest {
    private QuadTree tree;

    @BeforeEach
    void setUp() {
        tree = new QuadTree(new Vector2(0f, 0f), 10f);
    }

    @Test
    void testQuadTreeInitialization() {
        assertEquals(10f, tree.width);
        assertEquals(0f, tree.center.x);
        assertEquals(0f, tree.center.y);
    }
}