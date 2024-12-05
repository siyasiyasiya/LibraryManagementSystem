package network;

import java.io.*;
import java.net.*;
import database.*;

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
