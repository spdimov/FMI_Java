package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListServer {

    private static final int MAX_EXECUTOR_THREADS = 10;
    private final int SERVER_PORT;
    Map<String, Set<String>> studentToPresents;
    ExecutorService executor;

    public WishListServer(int port) {
        SERVER_PORT = port;
        studentToPresents = new HashMap<>();
        executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
    }

    public static void main(String[] args) {
        WishListServer wishListServer = new WishListServer(5555);
        wishListServer.start();
    }

    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                ClientRequestHandler clientHandler = new ClientRequestHandler(clientSocket, studentToPresents);

                executor.execute(clientHandler);
            }

        } catch (IOException e) {
            System.out.println("error");
        }
    }

    public void stop() {
        executor.shutdown();
    }
}
