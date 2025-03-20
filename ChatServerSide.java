import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServerSide {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static int clientCount = 0; // Unique client ID counter

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++; // Assign unique ID
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientCount);
                clients.add(clientHandler);
                clientHandler.start(); // Start new thread for each client
                System.out.println("New client connected: Client " + clientCount);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int clientId;

        public ClientHandler(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("You are Client " + clientId);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Client " + clientId + ": " + message);
                    broadcastMessage("Client " + clientId + ": " + message, this);
                }
            } catch (IOException e) {
                System.out.println("Client " + clientId + " disconnected.");
            } finally {
                removeClient();
                closeConnection();
            }
        }

        private void broadcastMessage(String message, ClientHandler sender) {
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    if (client != sender && client.out != null) {
                        client.out.println(message);
                    }
                }
            }
        }

        private void removeClient() {
            synchronized (clients) {
                clients.remove(this);
            }
            System.out.println("Client " + clientId + " removed.");
        }

        private void closeConnection() {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
