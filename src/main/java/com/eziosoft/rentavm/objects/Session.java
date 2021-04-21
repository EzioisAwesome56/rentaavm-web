package com.eziosoft.rentavm.objects;

public class Session {
    private String token;
    private boolean isActive;
    private long expire;
    private String owner;

    public Session(String token, long expire, String owner){
        this.token = token;
        this.isActive = true;
        this.expire = expire;
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public boolean isActive() {
        return isActive;
    }

    public long getExpire(){ return expire; }

    public String getOwner() { return owner; }
}
