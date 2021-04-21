package com.eziosoft.rentavm;

import com.google.gson.Gson;
import com.rethinkdb.net.Connection;
import com.rethinkdb.RethinkDB;

public class Database {

    private static Connection thonk;
    private static final RethinkDB r = RethinkDB.r;
    private static Gson gson = new Gson();

    public static void DatabaseInit(){
        System.out.println("Eziosoft Rentavm database driver starting up...");
        // build connection
        Connection.Builder builder = r.connection().hostname(main.conf.getDbip()).port(main.conf.getDbport());
        thonk = builder.connect();

        // basically lifted from Dankcord: check to see if our database actually friggin exists
        System.out.println("Checking if database exists...");
        if (!r.dbList().contains("rentavm").run(thonk, Boolean.class).first()){
            System.out.println("Database does not exist! creating it...");
            r.dbCreate("rentavm").run(thonk);
            r.db("rentavm").tableCreate("session").optArg("primary_key", "token").run(thonk);
            r.db("rentavm").tableCreate("users").optArg("primary_key", "username").run(thonk);
            r.db("rentavm").tableCreate("Vms").optArg("primary_key", "vmid").run(thonk);
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
        return r.table("users").get("username").count().eq(1).run(thonk, boolean.class).first();
    }
}
