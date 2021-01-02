package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ClientRequestHandler implements Runnable {

    Map<String, Set<String>> studentToPresents;
    private final Socket socket;

    public ClientRequestHandler(Socket socket, Map<String, Set<String>> studentToPresents) {
        this.socket = socket;
        this.studentToPresents = studentToPresents;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(processCommand(inputLine, studentToPresents));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
}
