package bg.sofia.uni.fmi.mjt.tagger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class WriteToFile {

    public static void writeToWriter(String line, Writer writer) {
        try (var bw = new BufferedWriter(writer)) {
            bw.write(line);
            bw.flush();

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}

