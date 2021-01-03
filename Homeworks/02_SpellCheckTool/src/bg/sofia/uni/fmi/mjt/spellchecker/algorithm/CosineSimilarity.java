package bg.sofia.uni.fmi.mjt.spellchecker.algorithm;

import java.util.HashMap;
import java.util.Map;


public class CosineSimilarity {
    final static int FIRST_WORD_POS = 0;
    final static int SECOND_WORD_POS = 1;

    public static double cosineSimilarity(String firstWord, String secondWord) {
        Map<String, Integer> firstWordGrammies = wordToGrammies(firstWord);
        Map<String, Integer> secondWordGrammies = wordToGrammies(secondWord);

        return vectorProduct(firstWordGrammies, secondWordGrammies)
               / (vectorLength(firstWordGrammies) * vectorLength(secondWordGrammies));
    }

    @SafeVarargs
    private static int vectorProduct(Map<String, Integer>... grammies) {
        int product = 0;
        for (String grammy : grammies[FIRST_WORD_POS].keySet()) {
            if (grammies[SECOND_WORD_POS].containsKey(grammy)) {
                product += grammies[FIRST_WORD_POS].get(grammy)
                           * grammies[SECOND_WORD_POS].get(grammy);
            }
        }

        return product;
    }

    private static double vectorLength(Map<String, Integer> grammies) {
        int length = 0;
        for (Integer grammyFrequency : grammies.values()) {
            length += Math.pow(grammyFrequency, 2);
        }

        return Math.sqrt(length);
    }

    private static Map<String, Integer> wordToGrammies(String word) {
        Map<String, Integer> grammies = new HashMap<>();

        int length = word.length();
        StringBuilder currentGrammy = new StringBuilder();

        for (int i = 0; i < length - 1; i++) {
            currentGrammy.append(word.charAt(i));
            currentGrammy.append(word.charAt(i + 1));
            String grammy = currentGrammy.toString();

            if (grammies.containsKey(grammy)) {
                grammies.put(grammy, grammies.get(grammy) + 1);
            } else {
                grammies.put(grammy, 1);
            }

            currentGrammy.delete(0, currentGrammy.length());
        }

        return grammies;
    }
}

