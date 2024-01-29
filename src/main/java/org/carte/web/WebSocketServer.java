package org.carte.web;

import lombok.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Builder
public class WebSocketServer {
    private String hostname;
    private int port;
    private int maxConnections;

    public void open() throws IOException, NoSuchAlgorithmException {
        try (var socket = new ServerSocket(port, maxConnections, InetAddress.getByName(hostname))) {


            System.out.println("Server has started on 127.0.0.1:8080.");
            for (int i = 0; i < maxConnections; i++) {
                System.out.println("Waiting for a connection…");
                Socket client = socket.accept();
                Thread.ofPlatform().start(() -> {
                    System.out.println("A client connected.");
                    try {
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        try (var s = new Scanner(in, StandardCharsets.UTF_8)) {
                            while (true) {
                                String data = s.useDelimiter("\\r\\n\\r\\n").next();
                                Matcher get = Pattern.compile("^GET").matcher(data);

                                if (get.find()) {
                                    Matcher match = Pattern.compile("[sS]ec-[wW]eb[sS]ocket-[kK]ey: (.*)").matcher(data);
                                    match.find();
                                    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                                            + "Connection: Upgrade\r\n"
                                            + "Upgrade: websocket\r\n"
                                            + "Sec-WebSocket-Accept: "
                                            + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                                            + "\r\n\r\n").getBytes("UTF-8");
                                    out.write(response, 0, response.length);
                                    byte[] decoded = new byte[10];
                                    byte[] encoded = new byte[10];
                                    byte[] key = new byte[]{(byte) 167, (byte) 225, (byte) 225, (byte) 210};
                                    for (int j = 0; j < encoded.length; j++) {
                                        decoded[j] = (byte) (encoded[j] ^ key[j & 0x3]);
                                    }

                                }
                            }
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });

            }
        }
    }
}
