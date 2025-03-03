package com.example.service;

import com.example.model.Particle;
import com.example.model.QuadTree;
import com.example.model.Vector2;
import java.util.ArrayList;
import java.util.List;


public class NBodyService {

    private int NUM_PARTICLES = 5000; // Supprimer `final` pour permettre la modification
    private Particle[] particles;
    private int[] seed;
    private QuadTree[] leafArray;
    private int[] leafIndex;

    // Pas de temps de base pour la stabilité
    private final float BASE_DT = 0.00005f;
    // currentDT est modifiable par l'utilisateur via setTimeStep()
    private float currentDT;

    private final float MAX_RADIUS = 13.0f;
    private final float PARTICLE_MASS =0.0002f;
    private float velocityFactor = 1500f;

    public NBodyService() {
        // Initialisation : dt est défini par BASE_DT * velocityFactor
        this.currentDT = BASE_DT * velocityFactor;
        initParticles();
    }

    private void initParticles() {
        particles = new Particle[NUM_PARTICLES];

        // Masse centrale très élevée pour ancrer les orbites
        float centralMass = 1e8f * PARTICLE_MASS;
        particles[0] = new Particle(0, new Vector2(0f, 0f), new Vector2(0f, 0f), centralMass);

        float minRadius = 1.0f;
        float stabilityFactor = 1.0f; // Facteur de correction pour assurer l’orbite

        for (int i = 1; i < NUM_PARTICLES; i++) {
            float r = minRadius + (float) Math.random() * (MAX_RADIUS - minRadius);
            float angle = (float) (2.0 * Math.PI * Math.random());
            float px = r * (float) Math.cos(angle);
            float py = r * (float) Math.sin(angle);

            float vx = 0f, vy = 0f;
            if (r > 0.001f) {
                // Calcul précis de la vitesse orbitale circulaire
                float orbitalSpeed = (float) Math.sqrt(QuadTree.G * centralMass / r);

                // Correction de stabilité pour éviter l'éloignement
                orbitalSpeed *= stabilityFactor;

                vx = -py / r * orbitalSpeed;
                vy =  px / r * orbitalSpeed;
            }

            particles[i] = new Particle(i, new Vector2(px, py), new Vector2(vx, vy), PARTICLE_MASS);
        }

        // Préparation du quadtree
        seed = new int[NUM_PARTICLES];
        for (int i = 0; i < NUM_PARTICLES; i++) {
            seed[i] = i;
        }
        leafArray = new QuadTree[NUM_PARTICLES];
        leafIndex = new int[1];
        leafIndex[0] = 0;
    }





    /**
     * Fait avancer la simulation d'un pas.
     */
    public void stepSimulation() {
        QuadTree root = new QuadTree(new Vector2(0f, 0f), 30f);
        leafIndex[0] = 0;
        root.check(particles, seed, NUM_PARTICLES, leafArray, leafIndex);
        propagateAll(root);
        root.decimate();
    }


    private void propagateAll(QuadTree root) {
        // Paramètre pour le softening
        float epsilon = 0.1f;
        // Seuil de distance en dessous duquel on n'applique que la force centrale
        float threshold = 1.5f;

        int totalLeaves = leafIndex[0];
        for (int i = 0; i < totalLeaves; i++) {
            QuadTree leaf = leafArray[i];
            for (int j = 0; j < leaf.population; j++) {
                int idx = leaf.indices[j];
                Particle p = particles[idx];
                // Ne mettez pas à jour le corps central fixe (index 0)
                if (idx == 0) continue;

                // 1. Calculer la force totale via Barnes-Hut (incluant force centrale + mutuelles)
                float[] axBH = new float[]{0f};
                float[] ayBH = new float[]{0f};
                root.traverse(leaf, p, particles, axBH, ayBH);

                // 2. Calculer la force centrale séparément (due au corps central, particles[0])
                float dx = p.position.x;  // distance du centre (0,0)
                float dy = p.position.y;
                float distSq = dx * dx + dy * dy + epsilon;
                float dist = (float) Math.sqrt(distSq);
                // Force centrale : F = G * M_central / (r^2 + epsilon)
                float centralForce = (QuadTree.G * particles[0].mass) / distSq;
                float axCentral = -centralForce * dx / dist;
                float ayCentral = -centralForce * dy / dist;

                // 3. Déduire la composante mutuelle :
                float axMutual = axBH[0] - axCentral;
                float ayMutual = ayBH[0] - ayCentral;

                // 4. Déterminer le facteur mutualScale en fonction de la distance :
                float r = dist;  // distance actuelle
                float mutualScale;
                if (r < threshold) {
                    mutualScale = 0f;
                } else {
                    // Par exemple, une échelle linéaire qui passe de 0 à 1 entre threshold et MAX_RADIUS
                    mutualScale = Math.min(1f, (r - threshold) / (MAX_RADIUS - threshold));
                }

                // 5. Définir l'accélération totale comme la somme de la force centrale
                // et de la force mutuelle atténuée
                float axTotal = axCentral + mutualScale * axMutual;
                float ayTotal = ayCentral + mutualScale * ayMutual;

                // Intégration Leapfrog (mise à jour en deux étapes pour une meilleure conservation d'énergie)
                // a. Mise à jour de la vitesse à mi-pas
                p.velocity.x += axTotal * (currentDT / 2);
                p.velocity.y += ayTotal * (currentDT / 2);

                // b. Mise à jour de la position complète
                p.position.x += p.velocity.x * currentDT;
                p.position.y += p.velocity.y * currentDT;

                // c. Recalcul de l'accélération à la nouvelle position
                axBH[0] = 0;
                ayBH[0] = 0;
                root.traverse(leaf, p, particles, axBH, ayBH);
                dx = p.position.x;
                dy = p.position.y;
                distSq = dx * dx + dy * dy + epsilon;
                dist = (float) Math.sqrt(distSq);
                centralForce = (QuadTree.G * particles[0].mass) / distSq;
                axCentral = -centralForce * dx / dist;
                ayCentral = -centralForce * dy / dist;
                axMutual = axBH[0] - axCentral;
                ayMutual = ayBH[0] - ayCentral;
                axTotal = axCentral + mutualScale * axMutual;
                ayTotal = ayCentral + mutualScale * ayMutual;

                // d. Compléter la mise à jour de la vitesse
                p.velocity.x += axTotal * (currentDT / 2);
                p.velocity.y += ayTotal * (currentDT / 2);
            }
        }
    }

    public List<Particle> getParticles() {
        List<Particle> list = new ArrayList<>();
        for (Particle p : particles) {
            if (p.index != 0) {  // Ne pas ajouter la masse centrale
                list.add(p);
            }
        }
        return list;
    }


    public void resetSimulation() {
        initParticles(); // Réinitialise avec le nombre par défaut (5000 par ex.)
    }

    public void resetSimulation(int numParticles) {
        this.NUM_PARTICLES = numParticles; // Mise à jour du nombre de particules
        initParticles();
    }



    /**
     * Permet à l'utilisateur de définir directement le pas de temps (DT).
     */
    public void setTimeStep(float dt) {

        this.currentDT = dt;
        System.out.printf("DT mis à jour : %.8f ", dt, velocityFactor);
    }

}
