package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.client.miniserver.MiniServer;
import bg.sofia.uni.fmi.mjt.client.miniserver.command.Command;
import bg.sofia.uni.fmi.mjt.client.miniserver.command.CommandCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Timer;

public class Client {

    private static final String CONNECT = "connect";
    private static final String DOWNLOAD = "download";
    private static final String REGISTER = "register";
    private static final String DISCONNECT = "disconnect";

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;
    private static final int miniServerPort = 5555;
    private static final int UPDATE_REQUEST_SECONDS = 30;

    private static final String CUSTOM_LINE_SEPARATOR = "\\,";

    String username;
    BufferedReader reader;
    PrintWriter writer;
    File usersInfo;

    Timer timer;
    MiniServer miniServer;

    public Client() {

        String pwd = System.getProperty("user.dir");
        usersInfo = new File(pwd + File.separator + "usersInfo.txt");
        if (!usersInfo.exists()) {
            try {
                System.out.println(usersInfo.createNewFile());
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
            writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            String message = "";
            while (!"disconnect".equals(message)) {
                System.out.print("Enter message: ");
                message = scanner.nextLine();
                Command cmd = CommandCreator.newCommand(message);


                switch (cmd.command()) {
                    case CONNECT -> connect(cmd.arguments(), socketChannel);
                    case DOWNLOAD -> new Thread(new DownloadRequest(reader, writer, usersInfo, cmd.arguments())).start();
                    case DISCONNECT -> disconnect();
                    default -> defaultCommandHandler(message);
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred in the client I/O: " + e.getMessage());
        }
    }

    private void connect(String[] args, SocketChannel socketChannel) throws IOException {
        miniServer = new MiniServer(miniServerPort);
        Thread miniServerThread = new Thread(miniServer);
        miniServerThread.setDaemon(true);
        miniServerThread.start();

        String user = args[0];
        this.username = user;

        writer.println(String.join(" ", CONNECT, user, socketChannel.socket().getInetAddress().getHostAddress(), String.valueOf(miniServerPort)));

        String reply = reader.readLine();
        System.out.println(reply);

        timer = new Timer();
        timer.schedule(new UpdateTimerTask(usersInfo), 0, UPDATE_REQUEST_SECONDS * 1000);

    }


    private void disconnect() throws IOException {
        writer.println("disconnect " + username);
        String response = reader.readLine();

        reader.close();
        writer.close();
        timer.cancel();

        System.out.println(response);
    }

    private void defaultCommandHandler(String message) throws IOException {
        writer.println(message);
        String reply = reader.readLine();
        System.out.println(reply.replace(CUSTOM_LINE_SEPARATOR, System.lineSeparator()).trim());
    }


}
