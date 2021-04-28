package com.eziosoft.rentavm.objects;

public class VirtualMachine {

    private String vmid;
    private String owner;
    private String ipaddr;
    private int port;

    public VirtualMachine(int id, String owner, String ip, int port){
        this.vmid = Integer.toString(id);
        this.owner = owner;
        this.ipaddr = ip;
        this.port = port;
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

    public void setIpaddr(String ipaddr) {
        this.ipaddr = ipaddr;
    }
}
