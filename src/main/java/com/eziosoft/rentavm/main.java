package com.eziosoft.rentavm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class main {
    public static void main(String[] args) throws IOException{
        // start by making a httpserver instance
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6969), 0);
        server.createContext("/", new Page());
        server.setExecutor(null);
        server.start();
        System.out.println("web server started");
    }

    static class Page implements HttpHandler{
        @Override
        public void handle(HttpExchange t) throws IOException{
            String what = "cool";
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes(StandardCharsets.UTF_8));
        }
    }


}
