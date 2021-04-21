package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.WebConf;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class main {

    public static WebConf conf;

    public static void main(String[] args) throws IOException{
        // quickly check to see if we have a config file present
        File fileconf = new File("config.json");
        if (!fileconf.exists()){
            System.out.println("Config file not found! Assuming defaults!");
            WebConf h = new WebConf();
            h.createDefaultConfig();
            FileWriter write = new FileWriter("config.json");
            Gson g = new Gson();
            write.write(g.toJson(h));
            write.close();
            conf = h;
        } else {
            System.out.println("Loading configuration data...");
            BufferedReader br = new BufferedReader(new FileReader("config.json"));
            Gson g = new Gson();
            conf = g.fromJson(br, WebConf.class);
            br.close();
        }
        // start by making a httpserver instance
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 6969), 0);
        server.createContext("/", new landing());
        server.createContext("/login", new login());
        server.createContext("/dologin", new dologin());
        server.setExecutor(null);
        server.start();
        System.out.println("web server started");
    }

    public static String getPageFromResource(String name){
        return new Scanner(main.class.getResourceAsStream(name), "UTF-8").useDelimiter("\\A").next();
    }

    // from https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange
    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    static class landing implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String what = getPageFromResource("/src/main.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());

        }
    }

    static class login implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String what = getPageFromResource("/src/login.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
        }
    }

    static class dologin implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            // get the post body
            Map<String, String> data = queryToMap(IOUtils.toString(t.getRequestBody(), Charset.defaultCharset()));
            System.out.println(data.get("username"));
            t.sendResponseHeaders(200, data.get("username").length());
            t.getResponseBody().write(data.get("username").getBytes());
        }
    }


}
