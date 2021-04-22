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
}
