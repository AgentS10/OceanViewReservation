package com.oceanview.server;

import com.oceanview.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

/**
 * Sets up and starts the plain-Java HTTP server on the configured port.
 * Uses {@code com.sun.net.httpserver.HttpServer} — no external server framework.
 * Registers all API route handlers and the static file handler.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class WebServer {

    private final int port;
    private HttpServer server;

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // API routes
        server.createContext("/api/auth",         new AuthHandler());
        server.createContext("/api/reservations", new ReservationHandler());
        server.createContext("/api/bill",         new BillHandler());
        server.createContext("/api/rooms",        new RoomHandler());
        server.createContext("/api/help",         new HelpHandler());
        server.createContext("/api/users",        new UserHandler());
        server.createContext("/api/admin",        new AdminHandler());

        // Static file serving — resolve web directory relative to working dir
        String webRoot = Paths.get("web").toAbsolutePath().toString();
        server.createContext("/", new StaticFileHandler(webRoot));

        // Use a thread pool so concurrent requests don't block each other
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("=========================================");
        System.out.println("  Ocean View Resort - Reservation System ");
        System.out.println("  Server running at: http://localhost:" + port);
        System.out.println("=========================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[Server] Stopped.");
        }
    }
}
