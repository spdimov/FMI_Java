package bg.sofia.uni.fmi.mjt.spellchecker.io;

import org.junit.Test;

import static bg.sofia.uni.fmi.mjt.spellchecker.preprocessing.Preprocessing.processDictionaryWord;
import static bg.sofia.uni.fmi.mjt.spellchecker.preprocessing.Preprocessing.processStopword;
import static org.junit.Assert.assertEquals;

public class PreprocessingUtilsTest {

    @Test
    public void processNonAlphaNumericTest() {
        String toTest = "$#$";
        String expectedResult = "";
        assertEquals(expectedResult, processDictionaryWord(toTest));
    }

    @Test
    public void processDictionaryWordTest() {
        String toTest = "  #$saMPle$ ";
        String expectedResult = "sample";
        assertEquals(expectedResult, processDictionaryWord(toTest));
    }

    @Test
    public void processStopwordTest() {
        String toTest = "  A  ";
        String expectedResult = "a";
        assertEquals(expectedResult, processStopword(toTest));
    }
}
