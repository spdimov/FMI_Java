package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class WishListClient {

    private static final int SERVER_PORT = 5555;

    String name;

    WishListClient(String name) {
        this.name = name;

        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
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
            System.out.println("error");
        }
    }

    public static void main(String[] args) {
        WishListClient wishListClient = new WishListClient("s");
    }
}
