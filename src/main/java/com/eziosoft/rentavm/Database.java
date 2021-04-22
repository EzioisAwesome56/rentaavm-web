package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.LogEntry;
import com.eziosoft.rentavm.objects.Session;
import com.eziosoft.rentavm.objects.User;
import com.google.gson.Gson;
import com.rethinkdb.net.Connection;
import com.rethinkdb.RethinkDB;

import java.rmi.server.ExportException;
import java.util.concurrent.TimeUnit;

public class Database {

    private static Connection thonk;
    private static final RethinkDB r = RethinkDB.r;
    private static Gson gson = new Gson();

    private static String user = "users";
    private static String session = "session";

    public static void DatabaseInit(){
        System.out.println("Eziosoft Rentavm database driver starting up...");
        // build connection
        Connection.Builder builder = r.connection().hostname(Main.conf.getDbip()).port(Main.conf.getDbport());
        thonk = builder.connect();

        // basically lifted from Dankcord: check to see if our database actually friggin exists
        System.out.println("Checking if database exists...");
        if (!r.dbList().contains("rentavm").run(thonk, Boolean.class).first()){
            System.out.println("Database does not exist! creating it...");
            r.dbCreate("rentavm").run(thonk);
            r.db("rentavm").tableCreate("session").optArg("primary_key", "token").run(thonk);
            r.db("rentavm").tableCreate("users").optArg("primary_key", "username").run(thonk);
            r.db("rentavm").tableCreate("vms").optArg("primary_key", "vmid").run(thonk);
            r.db("rentavm").tableCreate("logs").run(thonk);
            // some other third thing we might store in the database later idk
            //r.db("rentavm").tableCreate("auth").optArg("primary_key", "username").run(thonk);
            System.out.println("Database created!");
        } else {
            System.out.println("Database already exists!");
        }
        // bind our connection to it
        thonk.use("rentavm");
        // init done
        System.out.println("Database driver has finished starting up!");
    }

    public static boolean checkForUser(String username){
        // do cool things here
        if (!Main.dbEnabled){
            return Main.debugValue;
        }
        return r.table(user).getAll(username).count().eq(1).run(thonk, boolean.class).first();
    }

    public static void insertUser(User u){
        r.table(user).insert(u).run(thonk);
    }

    public static void insertSession(Session s){
        r.table(session).insert(s).run(thonk);
    }

    public static Session getSession(String token){
        return gson.fromJson(r.table(session).get(token).toJson().run(thonk, String.class).first(), Session.class);
    }

    public static User getUser(String username){
        return gson.fromJson(r.table(user).get(username).toJson().run(thonk, String.class).first(), User.class);
    }

    public static void deleteSession(String token){
        r.table(session).get(token).delete().run(thonk);
    }

    public static void deleteAllSessionsFromUser(String username){
        try {
            r.table(session).filter(r.hashMap("owner", username)).delete().run(thonk);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void writeLogEntry(LogEntry e){
        r.table("logs").insert(e).run(thonk);
    }

    public static boolean checkSessionValid(String token){
        // first we check to see if the session token returns any hits
        boolean exists = r.table(session).getAll(token).count().eq(1).run(thonk, boolean.class).first();
        if (!exists){
            return exists;
        }
        // if we have something, load it
        Session s = getSession(token);
        // is it expired?
        if (s.getExpire() < System.currentTimeMillis()){
            return false;
        }
        // is it set to inactive for some reason
        return s.isActive();
    }
}
