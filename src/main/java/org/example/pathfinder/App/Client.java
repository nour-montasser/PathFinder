package org.example.pathfinder.App;

import javafx.application.Platform;
import org.example.pathfinder.Model.LoggedUser;
import java.io.*;
import java.net.Socket;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 6655;
    private static final int RECONNECT_DELAY = 2000;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ClientListener listener;
    private volatile boolean connected;
    private Long userId;
    private Thread receiveThread;

    public Client() {
        this.userId = LoggedUser.getInstance().getUserId();
        connect();
    }

    private void connect() {
        try {            System.out.println("ashref");

            socket = new Socket(HOST, PORT);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            connected = true;
            System.out.println("[Client] Connected to server");
            sendInitialIdentification();
        } catch (IOException e) {
            System.err.println("[Client] Connection error: " + e.getMessage());
            connected = false;
            reconnect();
        }
    }

    private void reconnect() {
        while (!connected && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(RECONNECT_DELAY);
                connect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void sendInitialIdentification() {
        if (out != null && userId != null) {
            out.println("INIT:" + userId);
            out.flush();
            System.out.println("[Client] Sent initial identification: " + userId);
        }
    }

    public void start(ClientListener listener) {
        this.listener = listener;
        receiveThread = new Thread(this::receiveMessages);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void receiveMessages() {
        String message;
        try {
            while (connected && !Thread.currentThread().isInterrupted() && (message = in.readLine()) != null) {
                final String finalMessage = message;
                System.out.println("[Client] Received: " + finalMessage);
                Platform.runLater(() -> listener.onMessageReceived(finalMessage));
            }
        } catch (IOException e) {
            System.err.println("[Client] Error receiving message: " + e.getMessage());
            if (connected) {
                connected = false;
                reconnect();
            }
        }
    }

    public void sendTypingStatus(Long channelId, boolean isTyping) {
        if (!connected || out == null) {
            System.err.println("[Client] Cannot send typing status - not connected");
            return;
        }

        String status = "TYPING:" + channelId + ":" + isTyping;
        System.out.println("[Client] Sending typing status: " + status);
        out.println(status);
        out.flush();
    }

    public void sendMessage(String message, Long channelId, Long senderId) {
        if (!connected || out == null) {
            System.err.println("[Client] Cannot send message - not connected");
            return;
        }

        String formattedMessage = "MESSAGE:" + channelId + ":" + senderId + ":" + message;
        System.out.println("[Client] Sending: " + formattedMessage);
        out.println(formattedMessage);
        out.flush();
    }
    public void disconnect() {
        connected = false;
        try {
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("[Client] Error during disconnect: " + e.getMessage());
        }
    }
}