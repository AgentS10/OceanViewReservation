package com.oceanview;

import com.oceanview.database.DatabaseConnection;
import com.oceanview.observer.LogNotificationObserver;
import com.oceanview.observer.ReservationNotifier;
import com.oceanview.server.WebServer;

/**
 * Application entry point for Ocean View Resort Reservation System.
 * Initialises the database connection, registers observers, then starts the HTTP server.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26<br>
 * Ocean View Resort — Room Reservation System</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Initialise database (Singleton)
        DatabaseConnection.getInstance().initialize();

        // Register Observer for reservation event logging
        ReservationNotifier.getInstance().addObserver(new LogNotificationObserver());

        // Start HTTP server (plain Java — no Spring/Servlet framework)
        int port = 8081;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }

        WebServer server = new WebServer(port);
        server.start();

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
            DatabaseConnection.getInstance().close();
        }));
    }
}
