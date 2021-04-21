package com.eziosoft.rentavm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import static com.eziosoft.rentavm.main.getPageFromResource;
import static com.eziosoft.rentavm.main.queryToMap;

public class Pages {

    static class landing implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String what = getPageFromResource("/src/main.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
        }
    }

    static class login implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String filename = t.getRequestURI().getPath();
            System.out.println(filename);
            // check if resource exists
            if (main.class.getResource("/src" + filename) == null){
                String what = getPageFromResource("/404.html");
                t.sendResponseHeaders(404, what.length());
                t.getResponseBody().write(what.getBytes());
                t.getResponseBody().close();
                return;
            }
            if (filename.equals("/login/")){
                // return index.html
                String what = getPageFromResource("/src"+filename+"index.html");
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
            System.out.println(data.get("username"));
            t.sendResponseHeaders(200, data.get("username").length());
            t.getResponseBody().write(data.get("username").getBytes());
        }
    }
}
