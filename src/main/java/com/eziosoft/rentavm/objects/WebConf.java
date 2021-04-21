package com.eziosoft.rentavm.objects;

public class WebConf {
    private int dbport;
    private String dbip;
    private int webport;

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

    public void createDefaultConfig(){
        this.webport = 6969;
        this.dbport = 28015;
        this.dbip = "localhost";
    }
}
