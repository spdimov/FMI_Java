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

import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.DISCONNECTED_FROM_SERVER;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.GIFT_SUBMITTED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_USERNAME;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_USERNAME_PASSWORD;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_LOGGED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_REGISTERED_USER;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NO_STUDENTS_WISH_LIST;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NULL_PASSED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SAME_GIFT_SUBMITTED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SERVER_START_ERROR;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_LOGIN;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_LOGOUT;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_REGISTRATION;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.TAKEN_USERNAME;

public class WishListServer {

    int port;
    Map<String, Set<String>> usernameToPresents; //only users with at least one present in their wish list are presented
    Map<SocketChannel, String> channelToUsername; //only logged users are presented
    Map<String, String> registeredUsersToPassword;
    boolean isWorking;

    public WishListServer(int port) {
        this.port = port;
        usernameToPresents = new HashMap<>();
        channelToUsername = new HashMap<>();
        registeredUsersToPassword = new HashMap<>();
        isWorking = true;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (isWorking) {
                int readyChannels = selector.selectNow();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    if (key.isReadable()) {
                        handleReadableKey(key, buffer);
                    } else if (key.isAcceptable()) {
                        handleAcceptableKey(key, selector);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            System.out.println(SERVER_START_ERROR);
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        isWorking = false;

        for (SocketChannel sc : channelToUsername.keySet()) {
            sc.close();
        }
    }

    private void handleReadableKey(SelectionKey key, ByteBuffer buffer) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();

        buffer.clear();
        int r = sc.read(buffer);
        if (r < 0) {
            System.out.println("Client has closed the connection");
            sc.close();
        }

        buffer.flip();
        StringBuilder cmdBuilder = new StringBuilder();
        while (buffer.hasRemaining()) {
            cmdBuilder.append((char) buffer.get());
        }
        String command = cmdBuilder.toString().replace(System.lineSeparator(), "");

        if (!command.equals("disconnect")) {
            String answer = processCommand(command, sc);

            buffer.clear();
            buffer.put(answer.getBytes());
            buffer.put((byte) '\n');

            buffer.flip();
            sc.write(buffer);
        } else {

            buffer.clear();
            buffer.put(DISCONNECTED_FROM_SERVER.getBytes());
            buffer.put((byte) '\n');

            buffer.flip();
            sc.write(buffer);
            sc.close();
        }
    }

    private void handleAcceptableKey(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    String processCommand(String commandLine, SocketChannel socketChannel) {

        if (commandLine == null || socketChannel == null) {
            throw new IllegalArgumentException(NULL_PASSED);
        }

        final int COMMAND_POS = 0;
        String[] commandComponents = commandLine.split(" ");
        String command = commandComponents[COMMAND_POS];

        if (command.equals("post-wish") || command.equals("get-wish")
            || command.equals("disconnect") || command.equals("logout")) {
            if (!checkIfUserIsLogged(socketChannel)) {
                return NOT_LOGGED;
            }
        }

        return switch (command) {
            case "post-wish" -> postWish(commandComponents);
            case "get-wish" -> getWish(socketChannel);
            case "login" -> login(commandComponents, socketChannel);
            case "logout" -> logout(socketChannel);
            case "register" -> register(commandComponents, socketChannel);
            default -> "[ Unknown command ]";
        };

    }

    private boolean checkIfUserIsLogged(SocketChannel userSocketChannel) {
        return channelToUsername.containsKey(userSocketChannel);
    }

    String postWish(String[] commandComponents) {

        final int USERNAME_POS = 1;
        final int PRESENT_POS = 2;
        String username = commandComponents[USERNAME_POS];

        StringBuilder present = new StringBuilder();
        present.append(commandComponents[PRESENT_POS]);
        for (int pos = PRESENT_POS + 1; pos < commandComponents.length; pos++) {
            present.append(" ").append(commandComponents[pos]);
        }

        if (!isUserRegistered(username)) {
            return String.format(NOT_REGISTERED_USER, username);
        }

        String answer;
        if (usernameToPresents.containsKey(username)) { //Student already in wish list

            if (usernameToPresents.get(username).contains(present.toString())) {
                answer = String.format(SAME_GIFT_SUBMITTED, username);
            } else {
                usernameToPresents.get(username).add(present.toString());
                answer = String.format(GIFT_SUBMITTED, present.toString(), username);
            }

        } else { //Student is not in wish list
            Set<String> presents = new HashSet<>();
            presents.add(present.toString());
            usernameToPresents.put(username, presents);

            answer = String.format(GIFT_SUBMITTED, present.toString(), username);
        }

        return answer;
    }

    String getWish(SocketChannel userSocketChannel) {
        String exceptedUsername = channelToUsername.get(userSocketChannel);
        Set<String> removedPresents = usernameToPresents.get(exceptedUsername);

        if (isWishListEmpty() || wishListContainsOnlySameUser(exceptedUsername)) {
            return NO_STUDENTS_WISH_LIST;
        } else {

            usernameToPresents.remove(exceptedUsername);

            String randomUsername = (String) getRandomUser();
            String presents = usernameToPresents.get(randomUsername).toString();
            usernameToPresents.remove(randomUsername);

            if (removedPresents != null) {
                usernameToPresents.put(exceptedUsername, removedPresents);
            }

            return "[ " + randomUsername + ": " + presents + " ]";
        }
    }

    private boolean isWishListEmpty() {
        return usernameToPresents.size() == 0;
    }

    private boolean wishListContainsOnlySameUser(String exceptedUsername) {
        return usernameToPresents.size() == 1 && usernameToPresents.containsKey(exceptedUsername);
    }

    private Object getRandomUser() {
        Random generator = new Random();
        Object[] users = usernameToPresents.keySet().toArray();
        return users[generator.nextInt(users.length)];
    }

    String login(String[] commandComponents, SocketChannel userSocketChannel) {
        final int USERNAME_POS = 1;
        final int PASSWORD_POS = 2;
        String username = commandComponents[USERNAME_POS];
        String password = commandComponents[PASSWORD_POS];

        if (!isUserRegistered(username) || !checkUsernamePasswordCombination(username, password)) {
            return INVALID_USERNAME_PASSWORD;
        } else {
            channelToUsername.put(userSocketChannel, username);
            return String.format(SUCCESSFUL_LOGIN, username);
        }
    }

    String logout(SocketChannel socketChannel) {
        channelToUsername.remove(socketChannel);
        return SUCCESSFUL_LOGOUT;
    }

    String register(String[] commandComponents, SocketChannel userSocketChannel) {
        final int USERNAME_POS = 1;
        final int PRESENT_POS = 2;
        String username = commandComponents[USERNAME_POS];
        String password = commandComponents[PRESENT_POS];

        if (!isUsernameValid(username)) {
            return String.format(INVALID_USERNAME, username);
        } else if (isUserRegistered(username)) {
            return String.format(TAKEN_USERNAME, username);
        } else {
            registeredUsersToPassword.put(username, password);
            channelToUsername.put(userSocketChannel, username);
            return String.format(SUCCESSFUL_REGISTRATION, username);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.matches("[a-zA-Z0-9._-]+");
    }

    private boolean isUserRegistered(String username) {
        return registeredUsersToPassword.containsKey(username);
    }

    private boolean checkUsernamePasswordCombination(String username, String password) {
        return registeredUsersToPassword.get(username).equals(password);
    }
}