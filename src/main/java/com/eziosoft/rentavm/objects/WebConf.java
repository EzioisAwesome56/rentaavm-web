package com.eziosoft.rentavm.objects;

import org.mindrot.jbcrypt.BCrypt;

public class WebConf {
    private int dbport;
    private String dbip;
    private int webport;
    private String salt;

    public WebConf(int port, String dbip, int webport){
        this.dbip = dbip;
        this.dbport = port;
        this.webport = webport;
    }

    public WebConf(){};

    public int getDbport() {
        return dbport;
    }

    public int getWebport() {
        return webport;
    }

    public String getDbip() {
        return dbip;
    }

    public String getSalt() { return salt; }

    public void createDefaultConfig(){
        this.webport = 6969;
        this.dbport = 28015;
        this.dbip = "localhost";
        this.salt = BCrypt.gensalt();
    }
}
