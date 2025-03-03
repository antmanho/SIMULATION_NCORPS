package com.example;

import org.glassfish.tyrus.server.Server;
import com.example.controller.NBodyEndpoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) {
        // Lance le serveur WebSocket sur ws://localhost:8025/ws/nbody
        Server server = new Server("localhost", 8025, "/ws", null, NBodyEndpoint.class);


        try {
            server.start();
            System.out.println("Serveur WebSocket 2D démarré sur ws://localhost:8025/ws/nbody");
            System.out.println("Appuyez sur Entrée pour arrêter le serveur...");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
