package com.eziosoft.rentavm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
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
            String what = getPageFromResource("/src/login.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
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
