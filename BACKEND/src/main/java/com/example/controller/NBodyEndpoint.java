package com.example.controller;

import com.example.model.Particle;
import com.example.model.Vector2;
import com.example.service.NBodyService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/nbody")
public class NBodyEndpoint {

    public static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    public static final NBodyService service2D = new NBodyService();
    private static Thread simulationThread;
    private static boolean simulationRunning = false; // Suivi de l'état de la simulation
    private static final Gson gson = new GsonBuilder().create();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Client connecté (2D) : " + session.getId());
        startSimulationLoop();
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Client déconnecté (2D) : " + session.getId());
        if (sessions.isEmpty()) {
            stopSimulationLoop(); // Arrêter le thread s'il n'y a plus de clients
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message reçu (2D) de " + session.getId() + " : " + message);
        try {
            JsonObject obj = gson.fromJson(message, JsonObject.class);
            if (!obj.has("command")) {
                return;
            }
            String command = obj.get("command").getAsString();
            switch (command) {
                case "updateDt":
                    if (obj.has("dt")) {
                        float dt = obj.get("dt").getAsFloat();

                        service2D.setTimeStep(dt);
                        System.out.printf("DT mis à jour après relâchement du curseur : %.8f%n", dt);
                    }
                    break;



                case "resetSimulation":
                    if (obj.has("numParticles")) {
                        int numParticles = obj.get("numParticles").getAsInt();
                        service2D.resetSimulation(numParticles);
                    } else {
                        service2D.resetSimulation();
                    }
                    simulationRunning = true; // Relancer après réinitialisation
                    startSimulationLoop();
                    break;

                case "startSimulation":
                    simulationRunning = true;
                    startSimulationLoop();
                    break;

                case "stopSimulation":
                    simulationRunning = false;
                    break;

                default:
                    System.out.println("Commande inconnue : " + command);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        System.err.println("Erreur (2D) session " + session.getId() + " : " + t.getMessage());
    }

    private static synchronized void startSimulationLoop() {
        if (simulationThread != null && simulationThread.isAlive()) {
            return; // Ne pas recréer un thread si déjà actif
        }
        simulationThread = new Thread(() -> {
            System.out.println("Démarrage du thread de simulation 2D...");
            final long sleepMs = 30; // 30 ms ~ 33 FPS
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (simulationRunning) {
                        service2D.stepSimulation();
                        broadcast();
                    }
                    Thread.sleep(sleepMs);

                    if (sessions.isEmpty()) {
                        System.out.println("Plus de clients (2D), arrêt du thread de simulation.");
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        simulationThread.start();
    }

    private static synchronized void stopSimulationLoop() {
        simulationRunning = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
            simulationThread = null;
        }
    }

    private static void broadcast() {
        List<Particle> particles = service2D.getParticles();
        JsonObject jsonObj = new JsonObject();
        jsonObj.add("particles", gson.toJsonTree(particles));

        String json = gson.toJson(jsonObj);
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
