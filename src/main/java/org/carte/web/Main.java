package org.carte.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        WebSocketServer server = WebSocketServer.builder()
                .hostname("localhost")
                .port(8080)
                .maxConnections(1)
                .build();
        server.open();




    }
}