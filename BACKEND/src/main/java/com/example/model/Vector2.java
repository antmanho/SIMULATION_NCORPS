package com.example.model;

public class Vector2 {
    public float x;
    public float y;

    public Vector2() {
        this(0f, 0f);
    }
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Vector2(%.3f, %.3f)", x, y);
    }
}
