package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static bg.sofia.uni.fmi.mjt.tagger.ReadFromFile.addTags;
import static bg.sofia.uni.fmi.mjt.tagger.ReadFromFile.processLine;
import static org.junit.Assert.assertEquals;

public class TaggerTest {
    Tagger tagger;
    String cityCountryCSV = "Varna,BG" + System.lineSeparator() + "Sofia,BG" + System.lineSeparator() + "Dubai,UAE"+ System.lineSeparator() + "Plovdiv,BG";

    @Before
    public void setUp() {
        Reader reader = new StringReader(cityCountryCSV);
        tagger = new Tagger(reader);
    }

    @Test
    public void testReaderToMap() {
        Map<String, String> expectedCityToCountry = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        expectedCityToCountry.put("Varna", "BG");
        expectedCityToCountry.put("Sofia", "BG");
        expectedCityToCountry.put("Dubai", "UAE");
        expectedCityToCountry.put("Plovdiv", "BG");

        assertEquals(tagger.cityToCountry, expectedCityToCountry);
    }

    @Test
    public void testAddTags() {
        String city = "Varna";
        String expectedResult = "<city country=\"BG\">Varna</city>";

        assertEquals(expectedResult, addTags(city, tagger.cityToCountry));
    }

    @Test
    public void testProcessLine() {
        String line = "Plovdiv's old town is a major tourist attraction. It is the second largest city" +
                "in Bulgaria, after the capital ,Sofia.";

        String expectedResult = "<city country=\"BG\">Plovdiv</city>'s old town is a major tourist attraction. It is the second largest city" +
                "in Bulgaria, after the capital ,<city country=\"BG\">Sofia</city>.";

        assertEquals(expectedResult, processLine(line, tagger.cityToCountry));
    }

    @Test
    public void testNmostTaggedCities() {

        String text = "Plovdiv's old town is a major tourist attraction. It is the second largest city" + System.lineSeparator()
                + "in Bulgaria, after the capital ,Sofia.  Varna.Dubai Dubai Dubai Varna Varna Sofia Varna";
        Reader textReader = new BufferedReader(new StringReader(text));
        Writer writer = new BufferedWriter(new StringWriter(100));

        tagger.tagCities(textReader, writer);
        List<String> expectedResult = new LinkedList<>();
        expectedResult.add("Varna");
        expectedResult.add("Dubai");
        expectedResult.add("Sofia");

        assertEquals(tagger.getNMostTaggedCities(3), expectedResult);
    }
}
