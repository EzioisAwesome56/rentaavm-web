package com.eziosoft.rentavm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class main {
    public static void main(String[] args) throws IOException{
        // start by making a httpserver instance
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6969), 0);
        server.createContext("/", new landing());
        server.setExecutor(null);
        server.start();
        System.out.println("web server started");
    }

    public static String getPageFromResource(String name){
        return new Scanner(main.class.getResourceAsStream(name), "UTF-8").useDelimiter("\\A").next();
    }

    static class landing implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String what = getPageFromResource("/main.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());

        }
    }


}