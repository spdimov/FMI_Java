package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class WishListClient {

    private static final int SERVER_PORT = 5555;

    String name;

    WishListClient(String name) {
        this.name = name;

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                String message = scanner.nextLine();

                writer.println(message);

                String reply = reader.readLine();
                System.out.println(reply);

                if ("disconnect".equals(message)) {
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("error");
        }
    }
}
