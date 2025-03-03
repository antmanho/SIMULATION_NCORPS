package com.example.model;

import java.util.Arrays;

public class QuadTree {
    public static float G = 0.005f; // Constante gravitationnelle modifiable

    public float width;
    public float halfWidth;
    public float widthSq;
    public float mass = 0f;
    public int level = 0;
    public int population = 0;

    public Vector2 center;
    public Vector2 minPos;
    public Vector2 maxPos;

    public boolean leaf = true;
    public boolean populated = false;

    public int[] indices = null;
    public QuadTree[] children = new QuadTree[4];

    public QuadTree(Vector2 center, float w) {
        this.center = center;
        this.width = w;
        this.halfWidth = w / 2f;
        this.widthSq = w * w;

        this.minPos = new Vector2(center.x - halfWidth, center.y - halfWidth);
        this.maxPos = new Vector2(center.x + halfWidth, center.y + halfWidth);
    }

    public void decimate() {
        if (!leaf) {
            for (int i = 0; i < 4; i++) {
                if (children[i] != null) {
                    children[i].decimate();
                    children[i] = null;
                }
            }
        }
        indices = null;
    }

    public void check(Particle[] parts, int[] parentIndices, int parentSize,
                      QuadTree[] leafArray, int[] leafIndex) {
        int[] arr = new int[parentSize];
        for (int j = 0; j < parentSize; j++) {
            int idx = parentIndices[j];
            Particle p = parts[idx];
            if (p.position.x >= minPos.x && p.position.x < maxPos.x &&
                    p.position.y >= minPos.y && p.position.y < maxPos.y) {
                arr[population] = idx;
                population++;
                mass += p.mass;
                populated = true;
            }
        }
        if (population > 0) {
            indices = Arrays.copyOf(arr, population);
        }
        if (population > 1 && level < 20) {
            subdivide(parts, leafArray, leafIndex);
            leaf = false;
        } else {
            if (population > 0) {
                leafArray[leafIndex[0]] = this;
                leafIndex[0]++;
            }
        }
    }

    private void subdivide(Particle[] parts, QuadTree[] leafArray, int[] leafIndex) {
        float quarter = width / 4f;
        children[0] = new QuadTree(new Vector2(center.x + quarter, center.y + quarter), halfWidth);
        children[1] = new QuadTree(new Vector2(center.x - quarter, center.y + quarter), halfWidth);
        children[2] = new QuadTree(new Vector2(center.x - quarter, center.y - quarter), halfWidth);
        children[3] = new QuadTree(new Vector2(center.x + quarter, center.y - quarter), halfWidth);

        for (int i = 0; i < 4; i++) {
            children[i].level = this.level + 1;
            children[i].check(parts, indices, population, leafArray, leafIndex);
        }
    }

    /**
     * Traverse les nœuds pour appliquer l'algorithme Barnes-Hut.
     * Cette version **ne prend plus en compte de COM** mais garde l'effet gravitationnel attendu.
     */
    public void traverse(QuadTree node, Particle target, Particle[] all, float[] ax, float[] ay) {
        if (node == this) return;

        float epsilon = 0.5f;

        if (!leaf) {
            float dx = center.x - target.position.x;
            float dy = center.y - target.position.y;
            float distSq = dx * dx + dy * dy + epsilon * epsilon;

            if (widthSq / distSq < 1f) {
                float dist = (float) Math.sqrt(distSq);
                float force = (G * mass) / distSq;
                ax[0] += force * dx / dist;
                ay[0] += force * dy / dist;
            } else {
                for (QuadTree child : children) {
                    if (child != null) {
                        child.traverse(node, target, all, ax, ay);
                    }
                }
            }
        } else if (populated) {
            // Suppression complète de l'utilisation de COM
            float dx = center.x - target.position.x;
            float dy = center.y - target.position.y;
            float distSq = dx * dx + dy * dy + epsilon * epsilon;
            float dist = (float) Math.sqrt(distSq);
            float force = (G * mass) / distSq;
            ax[0] += force * dx / dist;
            ay[0] += force * dy / dist;
        }
    }
}
