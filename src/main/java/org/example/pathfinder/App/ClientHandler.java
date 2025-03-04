package org.example.pathfinder.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private Long senderId = -1L;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try {

            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("[Client] connected.");
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("[Server] Received: " + inputLine);

                if (inputLine.startsWith("INIT:")) {
                    senderId = Long.parseLong(inputLine.substring(5));
                    System.out.println("[Server] Client initialized with ID: " + senderId);
                    continue;
                }

                if (inputLine.startsWith("TYPING:")) {
                    handleTypingStatus(inputLine);
                    continue;
                }

                if (inputLine.startsWith("MESSAGE:")) {
                    handleMessage(inputLine);
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Error handling client: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleTypingStatus(String inputLine) {
        String[] parts = inputLine.split(":", 3);
        if (parts.length == 3) {
            Long channelId = Long.parseLong(parts[1]);
            boolean isTyping = Boolean.parseBoolean(parts[2]);
            Server.broadcastTypingStatus(channelId, this, isTyping);
        }
    }

    private void handleMessage(String inputLine) {
        String[] parts = inputLine.split(":", 4);
        if (parts.length == 4) {
            Long channelId = Long.parseLong(parts[1]);
            Long senderId = Long.parseLong(parts[2]);
            String message = parts[3];
            System.out.println("[Server] Broadcasting message from " + senderId + " to channel " + channelId);
            Server.broadcast(message, this, channelId);
        }
    }
    private void cleanup() {
        try {
            Server.clients.remove(this);
            socket.close();
        } catch (IOException e) {
            System.err.println("[Server] Error closing socket: " + e.getMessage());
        }
    }

}