package com.eziosoft.rentavm.objects;

public class session {
    private String session;
    private boolean isActive;

    public session(String token){
        this.session = token;
        this.isActive = true;
    }

    public String getSession() {
        return session;
    }

    public boolean isActive() {
        return isActive;
    }
}
