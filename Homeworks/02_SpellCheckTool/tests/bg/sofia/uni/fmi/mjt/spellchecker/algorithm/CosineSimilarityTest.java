package bg.sofia.uni.fmi.mjt.spellchecker.algorithm;

import org.junit.Test;

import static bg.sofia.uni.fmi.mjt.spellchecker.algorithm.CosineSimilarity.cosineSimilarity;
import static org.junit.Assert.assertEquals;

public class CosineSimilarityTest {

    @Test
    public void cosineSimilarityZeroTest() {
        String firstWord = "hello";
        String secondWord = "java";
        assertEquals(0, cosineSimilarity(firstWord, secondWord), 0.01);
    }

    @Test
    public void cosineSimilarityTest() {
        String word = "hello";
        String firstSimilarWord = "hallo";
        String secondSimilarWord = "phelo";
        assertEquals(0.5, cosineSimilarity(word, firstSimilarWord), 0.01);
        assertEquals(0.75, cosineSimilarity(word, secondSimilarWord), 0.01);
    }
}
