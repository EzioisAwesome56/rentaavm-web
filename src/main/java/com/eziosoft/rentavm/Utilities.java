package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.LogEntry;
import com.eziosoft.rentavm.objects.User;

import javax.xml.crypto.Data;

public class Utilities {

    public static void doLog(String msg, String owner){
        Database.writeLogEntry(new LogEntry(owner, msg));
    }

    public static String generateVMStats(String username){
        // first load the user in question
        User u = Database.getUser(username);
        // throw together a basic string
        String content = "<div id=\"box\">";
        // is the user's vmid 0?
        if (u.getVmid() == 0){
            content = content + "you currently do not own a vm<br>click <a href=\"/members/buy.html\">here</a> to obtain one";
        } else {
            // TODO: handle logic for checking the actual vm status or something like that idk
        }
        content += "</div>";
        return content;
    }

    public static String generateContainerStats(String username){
        // first load the user in question
        User u = Database.getUser(username);
        // throw together a basic string
        String content = "<div id=\"box\">";
        // is the user's vmid 0?
        if (u.getContid() == 0){
            content = content + "you currently do not own a container<br>click <a href=\"/members/buy.html\">here</a> to obtain one";
        } else {
            // TODO: handle logic for checking the actual container status or something like that idk
        }
        content += "</div>";
        return content;
    }

    public static String generateWindowsStats(String username){
        // first load the user in question
        User u = Database.getUser(username);
        // throw together a basic string
        String content = "<div id=\"box\">";
        // is the user's vmid 0?
        if (!u.getHasWindowsVM()[0]){
            content = content + "you currently have not purchased access to any Windows Virtual Machines on offer.<br>click <a href=\"/members/buy.html\">here</a> to purchase access";
        } else {
            // unlike the other 2, i already planned for part of this
            content += "you have access to the following Windows Virtual Machine:<br>";
            if (u.getHasWindowsVM()[1]){
                content += "Windows 10 64bit, 16gb ram, Dual cores, NVidia gtx960<br>";
                // TODO: actually query status for both of these virtual machines
            } else {
                content += "Windows 10 64bit, 16gb ram, Dual cores, NVidia gtx1060<br>";
            }
            content += "VM STATUS: Currently offline";
        }
        content += "</div>";
        return content;
    }
}
