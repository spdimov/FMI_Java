package bg.sofia.uni.fmi.mjt.spellchecker.preprocessing;

public class Preprocessing {
    public static String processDictionaryWord(String line) {
        String removeAtStartRegex = "^([^a-zA-Z0-9])+"; //remove leading non-alphanumeric symbols
        String removeAtEndRegex = "([^a-zA-Z0-9])+$"; //remove trailing non-alphanumeric symbols

        return line.trim()
                .toLowerCase()
                .replaceAll(removeAtStartRegex, "")
                .replaceAll(removeAtEndRegex, "");
    }

    public static String processStopword(String line) {
        return line.trim()
                .toLowerCase();
    }
}
