package org.example.pathfinder.App;

import org.example.pathfinder.Service.MessageService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 6655;
    public static final ArrayList<ClientHandler> clients = new ArrayList<>();
    private static final MessageService messageService = new MessageService();

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("[Server]::: is listening on port " + PORT);
            while (true) {
                System.out.println("ashref");

                Socket socket = serverSocket.accept();
                System.out.println("[Server] New client connected");
                ClientHandler clientConnection = new ClientHandler(socket);
                clients.add(clientConnection);
                try {
                    clientConnection.start();
                    System.out.println("clientConnection.start tekhdem");
                } catch (Exception e) {
                    System.err.println("Error starting client handler: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
        }
    }

    public static void broadcast(String message, ClientHandler sender, Long channelId) {
        System.out.println("[Server DEBUG] Broadcasting message to channel " + channelId);
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender && client.getSenderId() != null) {
                    try {
                        List<Long> clientChannels = messageService.getChannelsForUser(client.getSenderId());
                        if (clientChannels.contains(channelId)) {
                            System.out.println("[Server DEBUG] Sending to client " + client.getSenderId());
                            client.sendMessage("MESSAGE:" + message);
                        }
                    } catch (Exception e) {
                        System.err.println("[Server] Error broadcasting message: " + e.getMessage());
                    }
                }
            }
        }
    }
    public static void broadcastTypingStatus(Long channelId, ClientHandler sender, boolean isTyping) {
        System.out.println("[Server DEBUG] Broadcasting typing status - Channel: " + channelId + ", Sender: " + sender.getSenderId() + ", IsTyping: " + isTyping);
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != sender && client.getSenderId() != null) {
                    try {
                        List<Long> clientChannels = messageService.getChannelsForUser(client.getSenderId());
                        System.out.println("[Server DEBUG] Checking channels for client " + client.getSenderId() + ": " + clientChannels);
                        if (clientChannels.contains(channelId)) {
                            String message = "TYPING_STATUS:" + sender.getSenderId() + ":" + isTyping;
                            System.out.println("[Server DEBUG] Sending typing status to client " + client.getSenderId() + ": " + message);
                            client.sendMessage(message);
                        }
                    } catch (Exception e) {
                        System.err.println("[Server DEBUG] Error broadcasting typing status: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }}