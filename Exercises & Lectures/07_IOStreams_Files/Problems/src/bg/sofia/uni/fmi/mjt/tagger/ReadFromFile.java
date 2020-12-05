package bg.sofia.uni.fmi.mjt.tagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public class ReadFromFile {
    public static void readCityCountryToMap(Reader reader, Map<String, String> cityToCounty) {
        try (var br = new BufferedReader(reader)) {
            String line = "";

            while ((line = br.readLine()) != null) {
                String[] splitLineByComma = line.split(",");
                String city = splitLineByComma[0];
                String country = splitLineByComma[1];
                cityToCounty.put(city, country);
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static String processText(Reader reader, Map<String, String> cityToCountry, Map<String, Integer> cityToFrequency) {

        try (BufferedReader br = new BufferedReader(reader)) {
            String line = "";
            StringBuilder text = new StringBuilder();
            while ((line = br.readLine()) != null) {
                getFrequencyOfCitites(line, cityToCountry, cityToFrequency);
                line = processLine(line, cityToCountry);
                text.append(line).append(System.lineSeparator());
            }
            return text.toString();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static void getFrequencyOfCitites(String line, Map<String, String> cityToCountry, Map<String, Integer> cityToFrequency) {
        String[] words = line.split("((?<=[.,' ?!;])|(?=[.,' ?!;]))");

        for (String word : words) {
            if (cityToCountry.containsKey(word)) {
                if (cityToFrequency.containsKey(word)) {
                    cityToFrequency.put(word, cityToFrequency.get(word) + 1);
                } else {
                    cityToFrequency.put(word, 1);
                }
            }
        }
    }

    public static String processLine(String line, Map<String, String> cityToCountry) {
        String[] words = line.split("((?<=[.,' ?!;])|(?=[.,' ?!;]))");

        int realPos = 0;
        for (String word : words) {
            if (cityToCountry.containsKey(word)) {
                words[realPos] = addTags(word, cityToCountry);
            }
            realPos++;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String word : words) {
            stringBuilder.append(word);
        }

        return stringBuilder.toString();
    }

    public static String addTags(String city, Map<String, String> cityToCountry) {
        String country = cityToCountry.get(city);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<city country=\"");
        stringBuilder.append(country);
        stringBuilder.append("\">");
        stringBuilder.append(city);
        stringBuilder.append("</city>");

        return stringBuilder.toString();
    }

}
