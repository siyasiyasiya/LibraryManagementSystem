package network;

import java.io.*;
import java.net.*;
import database.*;

/**
 * Server class that establishes a connection with the client, forwarding the newly created socket to a ClientHandler,
 * which will handle the client's requests. The server creates a new thread for each client connection, ensuring
 * that users can connect simultaneously.
 *
 * <p>Purdue University -- CS18000 -- Fall 2024 -- Team Project</p>
 *
 * @author Siya Jariwala, Henry Hengyi Tsay, Nakshatra Tondepu, Saksham Kaushik
 * @version Nov 17, 2024
 */

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private boolean running;
    private Database database;

    public Server(int port) {
        database = new Database();

        try {
            serverSocket = new ServerSocket(port);
            running = true;
        } catch (IOException e) {
            System.out.println("FAILED CONNECTION");
        }

    }

    @Override
    public void run() {
        while (running) {
            try {

                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket, database);
                Thread thread = new Thread(clientHandler);
                thread.start();

            } catch (IOException e) {
                System.out.println("FAILED CONNECTION");
            }
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8000);
        Thread thread = new Thread(server);
        thread.start();
    }

}
