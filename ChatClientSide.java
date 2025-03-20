import java.io.*;
import java.net.*;

public class ChatClientSide {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 12345;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server.");
            System.out.println(in.readLine()); // Shows "You are Client X"

            // Start a separate thread to listen for server messages
            Thread listener = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Server connection lost. Exiting...");
                } finally {
                    System.exit(0); // Exit client properly
                }
            });

            listener.start();

            // Send messages to the server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }
}
