package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.storage.Storage;
import bg.sofia.uni.fmi.mjt.server.storage.UserInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class CommandExecutor {

    private static final String CONNECT = "connect";
    private static final String REGISTER = "register";
    private static final String UNREGISTER = "unregister";
    private static final String LIST_FILES = "list-files";
    private static final String UPDATE = "update";
    private static final String DISCONNECT = "disconnect";

    private static final String NOT_ACTIVE = "Specified user should be active for this operation";
    private static final String SUCCESSFUL_CONNECT = "Successfully connected";
    private static final String SUCCESSFUL_REGISTER = "Successfully registered files";
    private static final String SUCCESSFUL_UNREGISTER = "Successfully unregistered files";
    private static final String SUCCESSFUL_DISCONNECT = "Successfully disconnected";
    private static final String UNSUPPORTED_OPERATION = "Unsupported operation";
    private static final String INVALID_ARGUMENTS = "Invalid command arguments";

    private static final String CUSTOM_LINE_SEPARATOR = "\\,";

    private final Storage storage;

    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    public String execute(Command cmd) {
        if (CommandValidator.isCommandValid(cmd)) {
            return switch (cmd.command()) {
                case CONNECT -> connect(cmd.arguments());
                case REGISTER -> register(cmd.arguments());
                case UNREGISTER -> unregister(cmd.arguments());
                case LIST_FILES -> listFiles();
                case UPDATE -> update();
                case DISCONNECT -> disconnect(cmd.arguments());
                default -> UNSUPPORTED_OPERATION;
            };
        } else {
            return INVALID_ARGUMENTS;
        }
    }

    private String connect(String[] args) {
        String user = args[0];
        String addressIP = args[1];
        String miniServerPort = args[2];

        storage.connectUser(user, new UserInfo(addressIP, miniServerPort));

        return SUCCESSFUL_CONNECT;
    }

    private String register(String[] args) {
        String user = args[0];
        String[] files = Arrays.copyOfRange(args, 1, args.length);

        if (!storage.isUserActive(user)) {
            return NOT_ACTIVE;
        }

        storage.registerFiles(user, files);
        return SUCCESSFUL_REGISTER;
    }

    private String unregister(String[] args) {
        String user = args[0];
        String[] files = Arrays.copyOfRange(args, 1, args.length);

        if (!storage.isUserActive(user)) {
            return NOT_ACTIVE;
        }

        storage.unregisterFiles(user, files);
        return SUCCESSFUL_UNREGISTER;
    }

    private String listFiles() {
        Map<String, Set<String>> userFiles = storage.getUserFiles();

        StringBuilder response = new StringBuilder();
        for (String user : userFiles.keySet()) {
            for (String file : userFiles.get(user)) {
                response.append(user)
                        .append(" : ")
                        .append(file)
                        .append(CUSTOM_LINE_SEPARATOR);
            }
        }

        return response.toString();
    }

    private String update() {
        Map<String, UserInfo> activeUsers = storage.getActiveUsers();

        StringBuilder response = new StringBuilder();
        for (String user : activeUsers.keySet()) { //<user> - <ip:port>
            response.append(user)
                    .append(" - ")
                    .append(activeUsers.get(user).toString())
                    .append(CUSTOM_LINE_SEPARATOR);
        }

        return response.toString();
    }

    private String disconnect(String[] args) {
        if (!storage.isUserActive(args[0])) {
            return NOT_ACTIVE;
        }

        storage.disconnectUser(args[0]);
        return SUCCESSFUL_DISCONNECT;
    }
}

