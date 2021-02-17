package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.TimerTask;

public class UpdateTimerTask extends TimerTask {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;

    private static final String FILE_NOT_FOUND = "Specified file not found";
    private static final String UPDATE = "update";

    private static final String CUSTOM_LINE_SEPARATOR = "\\,";

    File usersInfo;

    public UpdateTimerTask(File usersInfo) throws IOException {
        this.usersInfo = usersInfo;
    }

    @Override
    public void run() {
        try (PrintWriter fileWriter = new PrintWriter(usersInfo.getAbsoluteFile());
             Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            writer.println(UPDATE);
            String response = reader.readLine().replace(CUSTOM_LINE_SEPARATOR,System.lineSeparator());
            fileWriter.println(response);

        } catch (FileNotFoundException e) {
            System.out.println(FILE_NOT_FOUND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

