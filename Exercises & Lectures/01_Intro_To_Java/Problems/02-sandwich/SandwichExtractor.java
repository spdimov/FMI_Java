import java.util.Arrays;

public class SandwichExtractor {
    public static final String bread = "bread";
    public static final String olives = "olives";

    public static String[] extractIngredients(String sandwich) {
        String[] ingredients = {};
        int startIndex = sandwich.indexOf(bread) + bread.length();
        int endIndex = sandwich.lastIndexOf(bread);
        if (startIndex - bread.length() == endIndex) {
            return ingredients;
        }
        String ingredientsString = sandwich.subSequence(startIndex, endIndex).toString();
        ingredients = ingredientsString.split("-");

        int olivesCount = 0;
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].equals(olives)) {
                olivesCount++;
            }
        }
        String[] ingredientsWithoutOlives = new String[ingredients.length - olivesCount];
        for (int i = 0, j = 0; i < ingredients.length; i++) {
            if (!ingredients[i].equals(olives)) {
                ingredientsWithoutOlives[j++] = ingredients[i];
            }
        }
        Arrays.sort(ingredientsWithoutOlives);
        return ingredientsWithoutOlives;
    }
}
