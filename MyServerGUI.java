import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class MyServerGUI extends Frame {
    private ServerSocket serverSocket;
    private List<ClientHandler> activeClients;
    private TextArea chatArea;
    private TextArea userArea;

    public MyServerGUI() {
        super("Chat Server");
        activeClients = new ArrayList<>();
        setupGUI();
        startServer();
    }

    private void setupGUI() {
        setLayout(new BorderLayout());

        // Chat display area
        chatArea = new TextArea();
        chatArea.setEditable(false);
        add(chatArea, BorderLayout.CENTER);

        // User list area
        userArea = new TextArea();
        userArea.setEditable(false);
        add(userArea, BorderLayout.EAST);

        // Set frame properties
        setSize(600, 400);
        setVisible(true);
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(6666);
            displayMessage("Server started. Waiting for clients...");

            // Thread to accept client connections
            Thread acceptClientsThread = new Thread(() -> {
                try {
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        activeClients.add(clientHandler);
                        clientHandler.start();
                        updateUserList();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            acceptClientsThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void updateUserList() {
        userArea.setText("Active Users:\n");
        for (ClientHandler client : activeClients) {
            userArea.append("- " + client.getUsername() + "\n");
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private DataInputStream din;
        private DataOutputStream dout;
        private String username;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                din = new DataInputStream(clientSocket.getInputStream());
                dout = new DataOutputStream(clientSocket.getOutputStream());

                // Ask for username
                username = din.readUTF();

                displayMessage(username + " joined the chat.");
                updateUserList();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public void run() {
            try {
                while (true) {
                    String receivedMessage = din.readUTF();
                    displayMessage(      receivedMessage);
                    // Broadcast message to all clients
                    for (ClientHandler client : activeClients) {
                        if (client != this) {
                            client.dout.writeUTF(username + ": " + receivedMessage);
                        }
                    }
                }
            } catch (IOException e) {
                // Client disconnected
                displayMessage(username + " left the chat.");
                activeClients.remove(this);
                updateUserList();
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new MyServerGUI();
    }
}
