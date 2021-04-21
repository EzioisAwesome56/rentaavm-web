package com.eziosoft.rentavm.objects;

public class Session {
    private String token;
    private boolean isActive;

    public Session(String token){
        this.token = token;
        this.isActive = true;
    }

    public String getToken() {
        return token;
    }

    public boolean isActive() {
        return isActive;
    }
}
