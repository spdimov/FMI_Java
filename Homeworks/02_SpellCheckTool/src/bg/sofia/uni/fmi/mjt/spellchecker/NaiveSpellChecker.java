package bg.sofia.uni.fmi.mjt.spellchecker;

import bg.sofia.uni.fmi.mjt.spellchecker.preprocessing.Preprocessing;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.spellchecker.algorithm.CosineSimilarity.cosineSimilarity;
import static bg.sofia.uni.fmi.mjt.spellchecker.io.ReadFile.fromReaderToSet;
import static bg.sofia.uni.fmi.mjt.spellchecker.io.ReadFile.readText;
import static bg.sofia.uni.fmi.mjt.spellchecker.preprocessing.Preprocessing.processDictionaryWord;

public class NaiveSpellChecker implements SpellChecker {

    final Set<String> dictionary;
    final Set<String> stopwords;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        dictionary = fromReaderToSet(dictionaryReader, Preprocessing::processDictionaryWord).stream()
                .filter(s -> s.length() > 1)
                .collect(Collectors.toUnmodifiableSet());

        stopwords = fromReaderToSet(stopwordsReader, Preprocessing::processStopword);
    }

    public static void main(String[] args) {
        System.out.println();
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        if (suggestionsCount < 1) {
            throw new IllegalArgumentException("Suggestions count should be positive number");
        }

        String text = readText(textReader);
        Reader copyReader = new StringReader(text);

        String metadata = createMetadataOutput(copyReader);
        String findings = createFindingsOutput(text, suggestionsCount);

        if (text.equals("")) {
            text += System.lineSeparator();
        }
        StringBuilder outputText = new StringBuilder(text);
        outputText.append(metadata);
        outputText.append(findings);

        try {
            output.write(outputText.toString());
            output.flush();
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while writing to a file", e);
        }
    }

    private String createMetadataOutput(Reader textReader) {
        StringBuilder metadataOutput = new StringBuilder();
        Metadata metadata = metadata(textReader);

        metadataOutput.append("= = = Metadata = = =").append(System.lineSeparator());
        metadataOutput.append(metadata.toString()).append(System.lineSeparator());

        return metadataOutput.toString();
    }

    private String createFindingsOutput(String text, int suggestionsCount) {
        StringBuilder findingsOutput = new StringBuilder();

        final Map<Integer, Set<String>> lineToMisspelledWords = getLineToMisspelledWords(text);
        findingsOutput.append("= = = Findings = = =").append(System.lineSeparator());
        findingsOutput.append(convertCustomMapToString(lineToMisspelledWords, suggestionsCount));

        return findingsOutput.toString();
    }

    private Map<Integer, Set<String>> getLineToMisspelledWords(String text) {
        Map<Integer, Set<String>> lineToMisspelledWords = new HashMap<>();

        String[] lines = text.split("(\\r\\n|\\r|\\n)+");
        int lineCounter = 1;

        for (String line : lines) {
            Set<String> misspelledWords = getMisspelledWords(line);

            if (!misspelledWords.isEmpty()) {
                lineToMisspelledWords.put(lineCounter, misspelledWords);
            }

            lineCounter++;
        }

        return lineToMisspelledWords;
    }

    private Set<String> getMisspelledWords(String line) {
        Set<String> misspelledWords = new HashSet<>();

        for (String word : line.split("\\s+")) {
            if (isWordMisspelled(word) && !word.isEmpty()) {
                misspelledWords.add(processDictionaryWord(word));
            }
        }

        return misspelledWords;
    }

    private String convertCustomMapToString(Map<Integer, Set<String>> lineToMisspelledWords, int suggestionCount) {
        StringBuilder result = new StringBuilder();

        for (Integer line : lineToMisspelledWords.keySet()) {
            result.append("Line #").append(line).append(", ");

            for (String word : lineToMisspelledWords.get(line)) {
                StringBuilder misspelledWordSuggestions = new StringBuilder("{"
                                                                            + word
                                                                            + "} - Possible suggestions are ");

                misspelledWordSuggestions.append(findClosestWords(word, suggestionCount).toString()
                        .replace("[", "{")
                        .replace("]", "}"));

                result.append(misspelledWordSuggestions).append(System.lineSeparator());
            }
        }

        return result.toString();
    }

    @Override
    public Metadata metadata(Reader textReader) {
        String text = readText(textReader);

        int chars = text.replaceAll("\\s+", "").length();
        TextInfo textInfo = countWords(text);

        return new Metadata(chars, textInfo.words(), textInfo.misspelledWords());
    }

    private TextInfo countWords(String text) {
        int wordCounter = 0;
        int misspelledCounter = 0;

        for (String word : text.split("[(\\r\\n|\\r|\\n) | \\s]+")) {
            if (isWordCountable(word)) {
                wordCounter++;

                if (isWordMisspelled(word)) {
                    misspelledCounter++;
                }
            }
        }

        return new TextInfo(wordCounter, misspelledCounter);
    }

    private boolean isWordCountable(String word) {
        return (!stopwords.contains(processDictionaryWord(word))
                && !word.matches("[^a-zA-Z0-9]+ | (^$)")
                && !word.matches("^$"));
    }

    public boolean isWordMisspelled(String word) {
        return (!dictionary.contains(processDictionaryWord(word))
                && !stopwords.contains((processDictionaryWord(word))));
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        if (word == null) {
            throw new IllegalArgumentException("Passed null as argument for word");
        }
        if (n < 1) {
            throw new IllegalArgumentException("N should be positive");
        }

        List<String> closestWords = new LinkedList<>(dictionary);
        closestWords.sort((d1, d2) -> Double.compare(cosineSimilarity(d2, word), cosineSimilarity(d1, word)));
        return closestWords.subList(0, n);
    }
}
