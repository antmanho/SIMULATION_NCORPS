package com.example.model;
import com.example.model.Vector2;
public class Particle {
    public int index;       // identifiant
    public Vector2 position;
    public Vector2 velocity;
    public float mass;

    // Accélération intermédiaire
    public float ax, ay;

    public Particle(int idx, Vector2 pos, Vector2 vel, float mass) {
        this.index = idx;
        this.position = pos;
        this.velocity = vel;
        this.mass = mass;
        this.ax = 0f;
        this.ay = 0f;
    }
}
