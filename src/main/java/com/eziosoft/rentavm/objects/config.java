package com.eziosoft.rentavm.objects;

public class config {
    private int dbport;
    private String dbip;
    private int webport;

    public config(int port, String dbip, int webport){
        this.dbip = dbip;
        this.dbport = port;
        this.webport = webport;
    }
}
