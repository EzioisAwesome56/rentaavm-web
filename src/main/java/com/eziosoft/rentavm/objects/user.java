package com.eziosoft.rentavm.objects;

public class user {
    private String username;
    private String passhash;
    private int vmid;
    private boolean[] hasWindowsVM;
    private int contid;

    public user(String username, String passhash){
        this.username = username;
        this.passhash = passhash;
        this.vmid = 0;
        this.hasWindowsVM = new boolean[]{false, false};
        this.contid = 0;
    }

    public boolean[] getHasWindowsVM() {
        return hasWindowsVM;
    }

    public int getContid() {
        return contid;
    }

    public int getVmid() {
        return vmid;
    }

    public String getPasshash() {
        return passhash;
    }

    public String getUsername() {
        return username;
    }
}
