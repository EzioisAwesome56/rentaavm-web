package com.eziosoft.rentavm.objects;

public class Session {
    private String token;
    private boolean active;
    private long expire;
    private String owner;

    public Session(String token, long expire, String owner){
        this.token = token;
        this.active = true;
        this.expire = expire;
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public boolean isActive() {
        return active;
    }

    public long getExpire(){ return expire; }

    public String getOwner() { return owner; }
}
