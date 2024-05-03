package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.server.storage.UserInfo;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class DownloadRequest implements Runnable {

    private static final String DOWNLOAD = "download";
    private static final String REGISTER = "register";

    private static final String FILE_NOT_FOUND = "Specified file not found";
    private static final String USER_NOT_FOUND = "Specified user not found";

    BufferedReader reader;
    PrintWriter writer;
    File usersInfo;
    String[] commandArgs;

    public DownloadRequest(BufferedReader reader, PrintWriter writer, File usersInfo, String[] commandArgs) {
        this.reader = reader;
        this.writer = writer;
        this.usersInfo = usersInfo;
        this.commandArgs = commandArgs;
    }

    private void download(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            String user = args[0];
            String wantedFilePath = args[1];
            String saveFilePath = args[2];

            UserInfo userInfo = getUserInfo(user);
            socketChannel.connect(new InetSocketAddress(userInfo.getAddressIP(), Integer.parseInt(userInfo.getMiniServerPort())));

            writer.println(String.join(" ", DOWNLOAD, user, wantedFilePath, saveFilePath));
            DataInputStream dataInputStream = new DataInputStream(socketChannel.socket().getInputStream());
            FileOutputStream fileOutputStream = new FileOutputStream("");

            int bytes = 0;
            byte[] buffer = new byte[Integer.MAX_VALUE];

            while ((bytes = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes);
            }

            writer.println(String.join(" ", REGISTER, user, saveFilePath));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private UserInfo getUserInfo(String user) {
        try (Scanner fileScanner = new Scanner(usersInfo)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();

                String currentUser = line.split("-")[0].trim();
                String userInfo = line.split("-")[1].trim();

                if (user.equals(currentUser)) {
                    String addressIP = userInfo.split(":")[0];
                    String miniServerPort = userInfo.split(":")[1];
                    return new UserInfo(addressIP, miniServerPort);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println(FILE_NOT_FOUND);
        }
        throw new IllegalArgumentException(USER_NOT_FOUND);
    }

    @Override
    public void run() {
        download(commandArgs);
    }
}
