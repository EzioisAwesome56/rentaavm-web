package com.eziosoft.rentavm;

import com.eziosoft.rentavm.objects.Session;
import com.eziosoft.rentavm.objects.User;
import com.eziosoft.rentavm.objects.VirtualMachine;
import com.rethinkdb.gen.ast.Http;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.helpers.Util;
import org.yaml.snakeyaml.Yaml;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.HttpCookie;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.eziosoft.rentavm.Main.getPageFromResource;
import static com.eziosoft.rentavm.Main.queryToMap;

public class Pages {

    private static Random random = new Random();

    // function to send error pages to the client
    private static void sendErrorPage(int errorcode, HttpExchange e) throws IOException {
        String errorPage = getPageFromResource("/src/error/" + Integer.toString(errorcode) + ".html");
        e.sendResponseHeaders(errorcode, errorPage.length());
        e.getResponseBody().write(errorPage.getBytes(StandardCharsets.UTF_8));
        // close the stream
        e.getResponseBody().close();
    }

    private static String getLoggedInUserName(HttpExchange e){
        String username = "guest";
        List<String> h = e.getRequestHeaders().get("Cookie");
        if (h != null){
            String token = h.get(0).split("=")[1];
            if (Database.checkSessionValid(token)){
                username = Database.getSession(token).getOwner();
            }
        }
        return username;
    }

    private static String getSessionToken(HttpExchange e){
        return e.getRequestHeaders().get("Cookie").get(0).split("=")[1];
    }

    static class landing implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String username = getLoggedInUserName(t);
            String what = getPageFromResource("/src/main.html");
            what = what.replace("{{$USER$}}", username);
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
        }
    }

    static class loginFolder implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String filename = t.getRequestURI().getPath();
            //debug shit System.out.println(filename);
            // check if resource exists
            if (Main.class.getResource("/src" + filename) == null){
                sendErrorPage(404, t);
                return;
            }
            if (filename.equals("/login/") || filename.equals("/login")){
                // return index.html
                String what = getPageFromResource("/src"+filename + "/" +"index.html");
                t.sendResponseHeaders(200, what.length());
                t.getResponseBody().write(what.getBytes());
                t.getResponseBody().close();
                return;
            }
            String what = getPageFromResource("/src"+filename);
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
            t.getResponseBody().close();
        }
    }

    static class dologin implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            // get the post body
            Map<String, String> data = queryToMap(IOUtils.toString(t.getRequestBody(), Charset.defaultCharset()));
            if (data.size() == 1 || !data.containsKey("username")){
                sendErrorPage(403, t);
                return;
            }
            if (data.get("username").isBlank() || data.get("password").isBlank()){
                sendErrorPage(582, t);
                return;
            }
            // check to see if username is valid/exists
            if (!Database.checkForUser(data.get("username"))){
                sendErrorPage(584, t);
                return;
            }
            // load user object from database
            User u = Database.getUser(data.get("username"));
            // compared password to hashed pass
            if (!BCrypt.hashpw(data.get("password"), Main.conf.getSalt()).equals(u.getPasshash())){
                Utilities.doLog("Failed login attempt for this user account", u.getUsername());
                sendErrorPage(583, t);
                return;
            }
            Session s = createSession(u);
            // delete any old sessions rotting away first
            Database.deleteAllSessionsFromUser(s.getOwner());
            Database.insertSession(s);
            t.getResponseHeaders().add("Set-Cookie", "token="+s.getToken()+"; Path=/");
            sendErrorPage(585, t);
            Utilities.doLog("user logged in", s.getOwner());
            return;
        }
    }

    static class doLogout implements HttpHandler{
        public void handle(HttpExchange e) throws IOException {
            if (e.getRequestHeaders().get("Cookie") == null){
                sendErrorPage(403, e);
                return;
            }
            String token = e.getRequestHeaders().get("Cookie").get(0).split("=")[1];
            Database.deleteSession(token);
            sendErrorPage(586, e);
            return;
        }
    }

    static class membersFolder implements HttpHandler{
        public void handle(HttpExchange e) throws IOException {
            // check to make sure the user is logged in (or atleast has a valid cookie)
            if (e.getRequestHeaders().get("Cookie") == null || !Database.checkSessionValid(getSessionToken(e))){
                sendErrorPage(401, e);
                return;
            }
            String requestedDocument = e.getRequestURI().getPath();
            if (Pages.class.getResource("/src" + requestedDocument) == null){
                sendErrorPage(404, e);
                return;
            }
            String page;
            if (requestedDocument.equals("/members/") || requestedDocument.equals("/members")){
                // return index.html
                page = getPageFromResource("/src" + requestedDocument + "/" + "index.html");
            } else {
                page = getPageFromResource("/src" + requestedDocument);
            }
            String user = getLoggedInUserName(e);
            // check what, if any, special processing is required for the page
            if (page.contains("{{$USER$}}")){
                page = page.replace("{{$USER$}}", user);
            }
            if (page.contains("{{$FREESTAT$}}")){
                page = page.replace("{{$FREESTAT$}}", Utilities.generateVMStats(user));
            }
            if (page.contains("{{$CONTSTAT$}}")){
                page = page.replace("{{$CONTSTAT$}}", Utilities.generateContainerStats(user));
            }
            if (page.contains("{{$WINSTAT$}}")){
                page = page.replace("{{$WINSTAT$}}", Utilities.generateWindowsStats(user));
            }
            // send the page that has finished processing
            e.sendResponseHeaders(200, page.length());
            e.getResponseBody().write(page.getBytes(StandardCharsets.UTF_8));
            e.getResponseBody().close();
        }
    }

    static class doRegister implements HttpHandler{
        public void handle(HttpExchange e) throws IOException {
            // get post data
            Map<String, String> data = queryToMap(IOUtils.toString(e.getRequestBody(), Charset.defaultCharset()));
            if (data.size() == 1 || !data.containsKey("username")){
                sendErrorPage(403, e);
                return;
            }
            // did the user leave any of the fields blank?
            if (data.get("username").isBlank() || data.get("email").isBlank() || data.get("pass1").isBlank() || data.get("pass2").isBlank()){
                sendErrorPage(580, e);
                return;
            }
            // check if provided passwords even match
            if (!data.get("pass1").equals(data.get("pass2"))){
                sendErrorPage(578, e);
                return;
            }
            // look and see if the username is taken
            if (Database.checkForUser(data.get("username"))){
                sendErrorPage(579, e);
                return;
            }
            // create new user object
            User u = new User(data.get("username"), BCrypt.hashpw(data.get("pass1"), Main.conf.getSalt()), data.get("email"));
            // insert into database
            Database.insertUser(u);
            Session s = createSession(u);
            Database.insertSession(s);
            // spit cookie onto client
            e.getResponseHeaders().add("Set-Cookie", "token="+s.getToken()+"; Path=/");
            sendErrorPage(581, e);
            Utilities.doLog("user account created", u.getUsername());
            return;
        }
    }

    private static Session createSession(User u){
        /* while here, lets take a moment to generate a session in the database
            a session entry has a few different parts
            - session id, generated from entered username, randomly generated values and system time, Bcrypted and base64'd
            - when it expires stored as miliseconds
            - what account it belongs too
             */
        String base = u.getUsername() + random.nextInt() + System.currentTimeMillis();
        String token = Base64.getEncoder().encodeToString(BCrypt.hashpw(base, BCrypt.gensalt()).getBytes(StandardCharsets.UTF_8));
        // make the session itself
        Session s = new Session(token, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(15), u.getUsername());
        return s;
    }

    static class registerFolder implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            // basically ripoff what we did for the login folder, but now for registrations!
            String requestedDocument = t.getRequestURI().getPath();
            if (Pages.class.getResource("/src" + requestedDocument) == null){
                sendErrorPage(404, t);
                return;
            }
            if (requestedDocument.equals("/register/") || requestedDocument.equals("/register")){
                // return index.html
                requestedDocument = getPageFromResource("/src" + requestedDocument + "/" + "index.html");
                t.sendResponseHeaders(200, requestedDocument.length());
                t.getResponseBody().write(requestedDocument.getBytes(StandardCharsets.UTF_8));
                t.getResponseBody().close();
                return;
            }
            String page = getPageFromResource("/src" + requestedDocument);
            t.sendResponseHeaders(200, page.length());
            t.getResponseBody().write(page.getBytes(StandardCharsets.UTF_8));
            t.getResponseBody().close();
        }
    }

    static class resetVM implements HttpHandler{
        @Override
        public void handle(HttpExchange e) throws IOException {
            // check if they have a valid session
            if (e.getRequestHeaders().get("Cookie") == null || !Database.checkSessionValid(getSessionToken(e))){
                sendErrorPage(401, e);
                return;
            }
            // get user name
            String user = getLoggedInUserName(e);
            // get user object
            User u = Database.getUser(user);
            // now we need to prepare the call to qm to reset the vm
            Runtime run = Runtime.getRuntime();
            try {
                String[] cmd = {"/bin/bash", "-c", "sudo qm reset "+u.getVmid()};
                Process qm = run.exec(cmd);
                qm.waitFor();
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            sendErrorPage(589, e);
        }
    }

    static class doVMCreate implements HttpHandler{
        @Override
        public void handle(HttpExchange e) throws IOException {
            // check if they have a valid session
            if (e.getRequestHeaders().get("Cookie") == null || !Database.checkSessionValid(getSessionToken(e))){
                sendErrorPage(401, e);
                return;
            }
            // first we should probably get the username of the person whos making the vm
            String user = getLoggedInUserName(e);
            // also we need post data
            Map<String, String> data = queryToMap(IOUtils.toString(e.getRequestBody(), Charset.defaultCharset()));
            // check to see if entered passwords work or not
            if (!data.get("pass1").equals(data.get("pass2"))){
                sendErrorPage(587, e);
                return;
            }
            if (Main.debug) {
                System.err.println("DEBUG: Now counting files...");
            }
            File configfolder = new File("/etc/pve/qemu-server");
            // count total number of vms in folder + subtract preset value of unrelated vms
            int filecount = configfolder.list().length - Main.conf.getSubtract();
            // add to base id + 1 to get new vm id
            int vmid = filecount + Main.conf.getStartId() + 1;
            if (Main.debug) {
                System.err.println("done counting files!");
            }
            /* VERY IMPORTANT NOTE:
            this programs requires either
            - running on root (please dont do this)
            or
            - password-less sudo access to the following commands on proxmox: qm, cp
            this is required for the proper cloning and shuffling around of cloudinit config files and whatever else so uh
            you can do this by using visudo and adding these lines to your file
            user host = (root) NOPASSWD: /usr/sbin/qm
            user host = (root) NOPASSWD: /usr/bin/cp
             */
            String[] cmd = {"/bin/bash", "-c", "sudo qm clone " + Main.conf.getTemplate() + " " + vmid};
            Process qm;
            Runtime run = Runtime.getRuntime();
            if (Main.debug){
                System.err.println("DEBUG: Now calling sub process...");
                System.err.println(cmd);
            }

            try {
                qm = run.exec(cmd);
                qm.waitFor();
                if (Main.debug) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(qm.getInputStream()));
                    String line = "";
                    while ((line = buf.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            } catch (Exception h){
                h.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // we need to generate the original cloudinit file now
            if (Main.debug){
                System.err.println("DEBUG: Setting initial cloudinit options");
            }
            try {
                cmd = new String[]{"/bin/bash", "-c", "sudo qm set " + vmid + " --ciuser \"" + data.get("user") + "\" --cipassword  \"" + data.get("pass1") + "\" --name \"" + vmid + "\""};
                qm = run.exec(cmd);
                qm.waitFor();
                if (Main.debug) {
                    BufferedReader buf = new BufferedReader(new InputStreamReader(qm.getInputStream()));
                    String line = "";
                    while ((line = buf.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // commit dump truck moment
            if (Main.debug){
                System.err.println("DEBUG: dumping cloud init file to string");
            }
            StringBuilder buiild = new StringBuilder();
            try {
                cmd = new String[]{"/bin/bash", "-c", "sudo qm cloudinit dump " + vmid + " user"};
                qm = run.exec(cmd);
                qm.waitFor();
                BufferedReader buf = new BufferedReader(new InputStreamReader(qm.getInputStream()));
                String line = "";
                while ((line = buf.readLine()) != null){
                    buiild.append(line + "\n");
                }
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // now we need to parse this yaml(ew)
            Yaml yaml = new Yaml();
            Map<String, Object> shit = yaml.load(buiild.toString());
            // get all the data we need out of this shitass YAML
            String username = shit.get("user").toString();
            String passhash = shit.get("password").toString();
            String host = shit.get("hostname").toString();
            if (Main.debug){
                System.err.println("Values obtained from YAML");
                System.err.println(username);
                System.err.println(passhash);
                System.err.println(host);
            }
            // next we need to load our "magic" presetup yaml file, and also inject what we need
            String finalconf = getPageFromResource("/src/base.yaml").replace("{{$HOST$}}", host).replace("{{$USER$}}", username).replace("{{$PW$}}", passhash);
            if (Main.debug){
                System.err.println("Final cloudinit YAML:");
                System.err.println(finalconf);
            }
            // next we need to write this file to a temp file
            File trash = File.createTempFile("patchyvm_temp", null);
            // write string to temp file
            BufferedWriter bufh = new BufferedWriter(new FileWriter(trash));
            bufh.write(finalconf);
            bufh.close();
            // ok now we have to cp the file somewhere
            if (Main.debug){
                System.err.println("PATH for temp file");
                System.err.println(trash.getAbsolutePath());
            }
            cmd = new String[]{"/bin/bash", "-c", "sudo cp " + trash.getAbsolutePath() + " /var/lib/vz/snippets/"+vmid};
            try {
                qm = run.exec(cmd);
                qm.waitFor();
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // im pretty sure at this point we can delete the temp file
            trash.delete();
            // set cicustom file
            cmd = new String[]{"/bin/bash", "-c", "sudo qm set " + vmid + " --cicustom \"user=local:snippets/"+vmid+"\""};
            try {
                qm = run.exec(cmd);
                qm.waitFor();
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // store the vm id of the vm in the user's oobject or something idk lol
            User u = Database.getUser(getLoggedInUserName(e));
            u.setVmid(vmid);
            Database.updateUser(u);
            // start the vm for the very, very first time
            cmd = new String[]{"/bin/bash", "-c", "sudo qm start "+vmid};
            try {
                qm = run.exec(cmd);
                qm.waitFor();
                // sleep for a few seconds
                TimeUnit.SECONDS.sleep(5);
                // restart the vm (TODO: Figure out why the vm keeps kernel panicing on system boot)
                cmd = new String[]{"/bin/bash", "-c", "sudo qm reset "+vmid};
                qm = run.exec(cmd);
                qm.waitFor();
            } catch (Exception ex){
                ex.printStackTrace();
                sendErrorPage(500, e);
                return;
            }
            // in theory that should be all we need!
            sendErrorPage(588, e);
            // ok now that the client is out of our hands we have to do one quick thing
            VirtualMachine vm = new VirtualMachine(Integer.toString(vmid), u.getUsername(), "null", 2);
            Database.insertVm(vm);
            // and now thats really everything we have to do!
        }
    }
}
