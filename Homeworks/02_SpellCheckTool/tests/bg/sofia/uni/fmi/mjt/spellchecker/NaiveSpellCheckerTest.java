package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaiveSpellCheckerTest {

    NaiveSpellChecker naiveSpellChecker;

    @Before
    public void setUp() {
        String dictionary = "This" + System.lineSeparator() +
                            "house" + System.lineSeparator() +
                            "heLLo%$  " + System.lineSeparator() +
                            "#!!java@  " + System.lineSeparator() +
                            "#@@$#" + System.lineSeparator() +
                            "I" + System.lineSeparator() +
                            "hellooo";

        String stopwords = "It" + System.lineSeparator() +
                           "a" + System.lineSeparator() +
                           "ArE" + System.lineSeparator() +
                           "  be ";

        try (
                Reader dictionaryReader =
                        new StringReader(dictionary);
                Reader stopwordsReader
                        = new StringReader(stopwords)
        ) {

            naiveSpellChecker = new NaiveSpellChecker(dictionaryReader, stopwordsReader);
        } catch (IOException e) {
            throw new IllegalStateException("Problem opening files");
        }


    }


    @Test
    public void metadataTest() {
        String text = "helLO" + System.lineSeparator() + "!!v@rna" + "  java?";
        Reader textReader = new StringReader(text);
        Metadata expectedResult = new Metadata(17, 3, 1);

        assertEquals(expectedResult, naiveSpellChecker.metadata(textReader));
    }

    @Test
    public void findClosestWordTest() {
        String word = "helo";
        List<String> expectedResult = new LinkedList<>();
        expectedResult.add("hello");

        assertEquals(expectedResult, naiveSpellChecker.findClosestWords(word, 1));
    }

    @Test
    public void analyzeTest() {
        String text = "Hello house helllo!" + System.lineSeparator() + "$java  it $a$";
        Reader textReader = new StringReader(text);
        Writer writer = new StringWriter();

        naiveSpellChecker.analyze(textReader, writer, 1);
        String expectedResult = """
                Hello house helllo!
                $java  it $a$
                = = = Metadata = = =
                27 characters, 4 words, 1 spelling issue(s) found
                = = = Findings = = =
                Line #1, {helllo} - Possible suggestions are {hello}
                """.replace("\n", System.lineSeparator());

        assertEquals(expectedResult, writer.toString());
    }

    @Test
    public void analyzeTestEmptyTextTest() {
        String text = "";
        Reader textReader = new StringReader(text);
        Writer writer = new StringWriter();

        naiveSpellChecker.analyze(textReader, writer, 1);
        String expectedResult = """
                              
                = = = Metadata = = =
                0 characters, 0 words, 0 spelling issue(s) found
                = = = Findings = = =
                """.replace("\n", System.lineSeparator());

        assertEquals(expectedResult, writer.toString());
    }
}
