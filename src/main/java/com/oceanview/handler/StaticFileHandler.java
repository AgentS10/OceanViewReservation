package com.oceanview.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves static files (HTML, CSS, JS, images) from the /web directory.
 * Handles MIME type detection and falls back to index.html for root requests.
 *
 * <p>CIS6003 Advanced Programming — WRIT1 Assignment 2025/26</p>
 *
 * @author Mohamed Subair Mohamed Sajidh
 * @version 2.0
 */
public class StaticFileHandler implements HttpHandler {

    private final String webRoot;

    public StaticFileHandler(String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCors(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
            return;
        }

        String uriPath = exchange.getRequestURI().getPath();
        if (uriPath.equals("/") || uriPath.isEmpty()) uriPath = "/index.html";

        Path filePath = Paths.get(webRoot, uriPath.replace("/", File.separator));

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            String notFound = "<html><body><h2>404 - File Not Found</h2></body></html>";
            byte[] bytes = notFound.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(404, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
            return;
        }

        String mime = getMimeType(filePath.toString());
        byte[] content = Files.readAllBytes(filePath);
        exchange.getResponseHeaders().set("Content-Type", mime);
        exchange.sendResponseHeaders(200, content.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(content); }
    }

    private String getMimeType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".ico"))  return "image/x-icon";
        return "application/octet-stream";
    }

    private void addCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET,OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}
