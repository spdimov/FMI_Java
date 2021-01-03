package bg.sofia.uni.fmi.mjt.spellchecker.io;

import bg.sofia.uni.fmi.mjt.spellchecker.preprocessing.ProcessLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public class ReadFile {

    public static Set<String> fromReaderToSet(Reader reader, ProcessLine processByType) {

        Set<String> words = new HashSet<>();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                line = processByType.processLine(line);

                words.add(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }

        return words;
    }

    public static String readText(Reader reader) {
        StringBuilder text = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                text.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from a file", e);
        }

        return text.toString();
    }


}
