package bg.sofia.uni.fmi.mjt.tagger;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static bg.sofia.uni.fmi.mjt.tagger.WriteToFile.writeToWriter;

public class Tagger {

    Map<String, String> cityToCountry;
    Map<String, Integer> cityToFrequency;

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */
    public Tagger(Reader citiesReader) {
        cityToCountry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        ReadFromFile.readCityCountryToMap(citiesReader, cityToCountry);
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) {

        cityToFrequency = new HashMap<>();
        String processedText = ReadFromFile.processText(text, cityToCountry, cityToFrequency).trim();
        writeToWriter(processedText, output);
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        List<String> orderedTaggedCities = new LinkedList<>(cityToFrequency.keySet());

        orderedTaggedCities.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.equals(o2)) {
                    return 0;
                } else if (cityToFrequency.get(o1) >= cityToFrequency.get(o2)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return orderedTaggedCities.subList(0, n);
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        return cityToFrequency.keySet();
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurences, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        long totalCount = 0;

        for (Integer tags : cityToFrequency.values()) {
            totalCount += tags;
        }

        return totalCount;
    }
}
