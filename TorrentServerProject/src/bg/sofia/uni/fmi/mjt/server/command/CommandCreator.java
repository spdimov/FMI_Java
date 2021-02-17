package bg.sofia.uni.fmi.mjt.server.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandCreator {

    private static List<String> getCommandArguments(String input) {
        List<String> tokens = new ArrayList<>();

        Collections.addAll(tokens, input.split("\\s+"));

        return tokens;
    }

    public static Command newCommand(String clientInput) {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String[] args = tokens.subList(1, tokens.size()).toArray(new String[0]);

        return new Command(tokens.get(0), args);
    }
}
