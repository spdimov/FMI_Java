package bg.sofia.uni.fmi.mjt.client.miniserver.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class CommandExecutorRunnable implements Runnable {

    private static final String DOWNLOAD = "download";
    private static final String SUCCESSFUL_DOWNLOAD = "Successful download";
    private static final String UNSUPPORTED_OPERATION = "Unsupported operation";

    private final Command cmd;
    private Socket clientSocket;

    public CommandExecutorRunnable(Command cmd, Socket clientSocket) {
        this.cmd = cmd;
        this.clientSocket = clientSocket;
    }

    private String execute(Command cmd, Socket clientSocket){
        this.clientSocket = clientSocket;

        if (DOWNLOAD.equals(cmd.command())) {
            return download(cmd.arguments());
        } else {
            return UNSUPPORTED_OPERATION;
        }
    }

    private String download(String[] args){
        File file = new File(args[0]);
        try(FileInputStream fileInputStream = new FileInputStream(file);) {

            int bytes = 0;
            byte[] buffer = new byte[Integer.MAX_VALUE];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                OutputStream outputStream = clientSocket.getOutputStream();
                outputStream.write(buffer, 0, bytes);
                outputStream.flush();
            }
        }
        catch (IOException e) {
           throw new UncheckedIOException(e);
        }
        return SUCCESSFUL_DOWNLOAD;
    }

    @Override
    public void run() {
        execute(cmd, clientSocket);
    }
}

