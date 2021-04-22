package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.LogEntry;

import javax.xml.crypto.Data;

public class Utilities {

    public static void doLog(String msg, String owner){
        Database.writeLogEntry(new LogEntry(owner, msg));
    }
}
