package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.WebConf;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static WebConf conf;
    public static boolean dbEnabled;
    public static boolean debugValue;

    public static void main(String[] args) throws IOException{
        dbEnabled = true;
        debugValue = false;
        // Optional Command Line Parameters parasing
        if (Arrays.stream(args).anyMatch("-help"::contains)){
            System.out.println("Rentavm help");
            System.out.println("-nodb: disables database initilization and in some cases functions");
            System.out.println("-true: sets debug return value to true (used in very few commands");
            System.exit(0);
        }
        if (Arrays.stream(args).anyMatch("-nodb"::contains)){
            System.out.println("running web interface without database functionality!");
            dbEnabled = false;
        }
        if (Arrays.stream(args).anyMatch("-true"::contains)){
            System.out.println("Set debug return value to true");
            debugValue = true;
        }

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
        // do we init the db?
        if (dbEnabled){
            Database.DatabaseInit();
        }

        // start by making a httpserver instance
        HttpServer server = HttpServer.create(new InetSocketAddress(conf.getWebip(), conf.getWebport()), 0);
        server.createContext("/", new Pages.landing());
        server.createContext("/login", new Pages.loginFolder());
        server.createContext("/api/dologin", new Pages.dologin());
        server.createContext("/register", new Pages.registerFolder());
        server.createContext("/api/doregister", new Pages.doRegister());
        server.createContext("/api/dologout", new Pages.doLogout());
        server.createContext("/members", new Pages.membersFolder());
        server.setExecutor(null);
        server.start();
        System.out.println("web server started");
    }

    public static String getPageFromResource(String name){
        return new Scanner(Main.class.getResourceAsStream(name), "UTF-8").useDelimiter("\\A").next();
    }

    // from https://stackoverflow.com/questions/11640025/how-to-obtain-the-query-string-in-a-get-with-java-httpserver-httpexchange
    static Map<String, String> queryToMap(String query) {
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


}
