package com.eziosoft.rentavm.objects;

public class VirtualMachine {

    private String vmid;
    private String owner;
    private String ipaddr;
    private int port;

    public VirtualMachine(int a, String b, String c, int d){
        this.vmid = Integer.toString(a);
        this.owner = b;
        this.ipaddr = c;
        this.port = d;
    }

    public int getVmid() {
        return Integer.parseInt(vmid);
    }

    public int getPort() {
        return port;
    }

    public String getIpaddr() {
        return ipaddr;
    }

    public String getOwner() {
        return owner;
    }
}
