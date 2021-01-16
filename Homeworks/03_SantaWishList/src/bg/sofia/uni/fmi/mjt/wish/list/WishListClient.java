package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class WishListClient {

    private static final int SERVER_PORT = 5555;

    WishListClient() {

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress("localhost", SERVER_PORT));

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
            System.out.println("Not connected");
        }
    }
}