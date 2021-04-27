package com.eziosoft.rentavm.objects;

import org.mindrot.jbcrypt.BCrypt;

public class WebConf {
    private int dbport;
    private String dbip;
    private int webport;
    private String salt;
    private int startId;
    private int subtract;
    private String template;
    private String webip;

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

    public int getStartId() {
        return startId;
    }

    public int getSubtract() {
        return subtract;
    }

    public String getTemplate() {
        return template;
    }

    public String getWebip() {
        return webip;
    }

    public void createDefaultConfig(){
        this.webport = 6969;
        this.dbport = 28015;
        this.dbip = "localhost";
        this.salt = BCrypt.gensalt();
        this.startId = 200;
        this.subtract = 0;
        this.webip = "localhost";
        this.template = "103";
    }
}
