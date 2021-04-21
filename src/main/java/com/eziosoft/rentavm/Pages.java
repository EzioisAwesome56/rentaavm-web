package com.eziosoft.rentavm;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.eziosoft.rentavm.main.getPageFromResource;
import static com.eziosoft.rentavm.main.queryToMap;

public class Pages {

    // function to send error pages to the client
    private static void sendErrorPage(int errorcode, HttpExchange e) throws IOException {
        String errorPage;
        switch (errorcode){
            case 404:
                errorPage = getPageFromResource("/src/error/404.html");
                break;
            case 403:
                errorPage = getPageFromResource("/src/error/403.html");
                break;
            default:
                errorPage = "Error while processing error!";
                break;
        }
        e.sendResponseHeaders(errorcode, errorPage.length());
        e.getResponseBody().write(errorPage.getBytes(StandardCharsets.UTF_8));
        // close the stream
        e.getResponseBody().close();
    }

    static class landing implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String what = getPageFromResource("/src/main.html");
            t.sendResponseHeaders(200, what.length());
            t.getResponseBody().write(what.getBytes());
        }
    }

    static class loginFolder implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            String filename = t.getRequestURI().getPath();
            //debug shit System.out.println(filename);
            // check if resource exists
            if (main.class.getResource("/src" + filename) == null){
                sendErrorPage(404, t);
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

    static class registerFolder implements HttpHandler{
        public void handle(HttpExchange t) throws IOException{
            // basically ripoff what we did for the login folder, but now for registrations!
            String requestedDocument = t.getRequestURI().getPath();
            if (Pages.class.getResource("/src" + requestedDocument) == null){
                sendErrorPage(404, t);
                return;
            }
            if (requestedDocument.equals("/register/")){
                // return index.html
                requestedDocument = getPageFromResource("/src" + requestedDocument + "index.html");
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
}
