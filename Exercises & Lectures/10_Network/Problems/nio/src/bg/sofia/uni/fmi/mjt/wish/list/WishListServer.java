package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WishListServer {

    private static final int MAX_EXECUTOR_THREADS = 10;
    private final int SERVER_PORT;
    Map<String, Set<String>> studentToPresents;
    ExecutorService executor;
    boolean isWorking;

    public WishListServer(int port) {
        SERVER_PORT = port;
        studentToPresents = new HashMap<>();
        executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);
        isWorking = true;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress("localhost", SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (isWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    // select() is blocking but may still return with 0, check javadoc
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel) key.channel();

                        buffer.clear();
                        int r = sc.read(buffer);
                        if (r < 0) {
                            System.out.println("Client has closed the connection");
                            sc.close();
                            continue;
                        }

                        buffer.flip();
                        StringBuilder cmd = new StringBuilder();
                        while (buffer.hasRemaining()) {
                            cmd.append((char) buffer.get());
                        }
                        String answer = processCommand(cmd.toString().replace(System.lineSeparator(), ""), studentToPresents);

                        buffer.clear();
                        buffer.put(answer.getBytes());
                        buffer.put((byte) '\n');

                        buffer.flip();
                        sc.write(buffer);

                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false);
                        accept.register(selector, SelectionKey.OP_READ);
                    }

                    keyIterator.remove();
                }

            }

        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            e.printStackTrace();
        }
    }

    public void stop() {
        isWorking = false;
    }

    private String processCommand(String commandLine, Map<String, Set<String>> studentToPresents) {
        final int COMMAND_POS = 0;

        String[] commandComponents = commandLine.split(" ");

        String command = commandComponents[COMMAND_POS];

        return switch (command) {
            case "post-wish" -> postWish(commandLine, studentToPresents);
            case "get-wish" -> getWish(commandLine, studentToPresents);
            case "disconnect" -> "[ Disconnected from server ]";
            default -> "[ Unknown command ]";
        };

    }

    private String postWish(String commandLine, Map<String, Set<String>> studentToPresents) {

        String[] commandComponents = commandLine.split(" ");

        String student = commandComponents[1];
        String present = commandLine.substring(commandLine.indexOf(" ", 10) + 1);

        String answer = "";
        if (studentToPresents.containsKey(student)) { //Student already in wish list
            if (studentToPresents.get(student).contains(present.toString())) {
                answer = "[ The same gift for student " + student + " was already submitted ]";

            } else {
                studentToPresents.get(student).add(present.toString());

                answer = "[ Gift " + present + " for student " + student + " submitted successfully ]";
            }

        } else { //Student is not in wish list
            Set<String> presents = new HashSet<>();
            presents.add(present.toString());
            studentToPresents.put(student, presents);

            answer += "[ Gift " + present + " for student " + student + " submitted successfully ]";
        }

        return answer;
    }

    private String getWish(String commandLine, Map<String, Set<String>> studentToPresents) {

        String answer = "";
        if (!studentToPresents.isEmpty()) {
            Random generator = new Random();
            Object[] values = studentToPresents.keySet().toArray();
            Object randomValue = values[generator.nextInt(values.length)];

            answer += "[ " + randomValue + ": " + studentToPresents.get(randomValue).toString() + " ]";
            studentToPresents.remove(randomValue);
        } else {
            answer += "[ There are no students present in the wish list ]";
        }

        return answer;
    }

    public static void main(String[] args) throws InterruptedException {
        WishListServer wishListServer = new WishListServer(5555);
        wishListServer.start();


    }
}
