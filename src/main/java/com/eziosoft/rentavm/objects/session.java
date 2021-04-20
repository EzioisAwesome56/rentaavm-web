package com.eziosoft.rentavm.objects;

public class session {
    private String token;
    private boolean isActive;

    public session(String token){
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
