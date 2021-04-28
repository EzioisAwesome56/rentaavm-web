package com.eziosoft.rentavm.objects;

import com.eziosoft.rentavm.Database;
import com.eziosoft.rentavm.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PhoneClient {

    final Socket s;
    final DataInputStream in;
    final DataOutputStream out;
    private String vmid;
    private String ip;


    public PhoneClient(Socket s, DataInputStream in, DataOutputStream out){
        this.s = s;
        this.in = in;
        this.out = out;
    }

    public void handle(){
        Thread loop = new Thread(() -> {
            try {
                // get ip
                ip = ((InetSocketAddress) s.getRemoteSocketAddress()).getAddress().toString().split("/")[1];
                // get hostname
                vmid = in.readUTF();
                if (Main.debug){
                    System.err.println(vmid);
                }
                // we dont need the client anymore, kill it
                s.close();
                out.close();
                in.close();
                // check to see if vmid is valid
                if (!Database.checkForVM(vmid)){
                    System.err.println("Invalid update attempt detected");
                    return;
                }
                // otherwise, load the vm
                VirtualMachine vm = Database.getVM(vmid);
                // update ipaddr
                vm.setIpaddr(ip);
                Database.updateVM(vm);
                // and we're done
                return;
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        loop.start();
    }
}
