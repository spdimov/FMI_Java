package echoclientserver.net.simple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

    private static final int SERVER_PORT = 6666;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT);) {
            System.out.println("Server started and listening for connect request");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());

            try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    System.out.println("Message received from client: " + inputLine);
                    out.println("Echo " + inputLine);
                }
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            e.printStackTrace();
        }
    }

}
