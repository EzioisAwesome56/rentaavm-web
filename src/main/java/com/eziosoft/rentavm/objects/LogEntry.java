package com.eziosoft.rentavm.objects;

public class LogEntry {

    private long time;
    private String owner;
    private String logmessage;

    public LogEntry(String owner, String msg){
        this.logmessage = msg;
        this.owner = owner;
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public String getLogmessage() {
        return logmessage;
    }

    public String getOwner() {
        return owner;
    }
}
