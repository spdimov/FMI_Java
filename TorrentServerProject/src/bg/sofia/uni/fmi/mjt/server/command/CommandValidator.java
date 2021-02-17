package bg.sofia.uni.fmi.mjt.server.command;

public class CommandValidator {

    private static final String CONNECT = "connect";
    private static final String REGISTER = "register";
    private static final String UNREGISTER = "unregister";
    private static final String LIST_FILES = "list-files";
    private static final String UPDATE = "update";
    private static final String DISCONNECT = "disconnect";

    public static boolean isCommandValid(Command cmd) {
        return switch (cmd.command()) {
            case CONNECT -> isConnectValid(cmd.arguments());
            case REGISTER -> isRegisterValid(cmd.arguments());
            case UNREGISTER -> isUnregisterValid(cmd.arguments());
            case LIST_FILES -> isListFilesValid(cmd.arguments());
            case UPDATE -> isUpdateValid(cmd.arguments());
            case DISCONNECT -> isDisconnectValid(cmd.arguments());
            default -> true;
        };
    }

    private static boolean isConnectValid(String[] args) {
        return args.length == 3;
    }

    private static boolean isRegisterValid(String[] args) {
        return args.length >= 2;
    }

    private static boolean isUnregisterValid(String[] args) {
        return args.length >= 2;
    }

    private static boolean isListFilesValid(String[] args) {
        return args.length == 0;
    }

    private static boolean isUpdateValid(String[] args) {
        return args.length == 0;
    }

    private static boolean isDisconnectValid(String[] args) {
        return args.length == 1;
    }
}
