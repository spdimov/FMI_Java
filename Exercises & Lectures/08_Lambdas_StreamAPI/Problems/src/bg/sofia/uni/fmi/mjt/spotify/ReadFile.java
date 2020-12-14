package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class ReadFile {

    public static void readFromFileToList(Reader input, List<SpotifyTrack> trackList) {
        try (BufferedReader br = new BufferedReader(input)) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    trackList.add(SpotifyTrack.of(line));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
