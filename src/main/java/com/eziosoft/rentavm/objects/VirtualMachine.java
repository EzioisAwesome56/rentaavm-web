package com.eziosoft.rentavm.objects;

public class VirtualMachine {

    private String vmid;
    private String owner;
    private String ipaddr;
    private int port;

    public VirtualMachine(String id, String owner, String ip, int port){
        this.vmid = id;
        this.owner = owner;
        this.ipaddr = ip;
        this.port = port;
    }

    public String getVmid() {
        return vmid;
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

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }
}
