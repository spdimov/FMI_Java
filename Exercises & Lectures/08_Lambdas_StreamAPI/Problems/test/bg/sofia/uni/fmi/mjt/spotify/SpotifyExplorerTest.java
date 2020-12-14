package bg.sofia.uni.fmi.mjt.spotify;

import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SpotifyExplorerTest {
    SpotifyExplorer testSpotifyExplorer;

    @Before
    public void setUp() {

        String dataset = "4BJqT0PrAfrxzMOxytFOIz,['Sergei Rachmaninoff'; 'James Levine'; 'Berliner Philharmoniker'],Piano Concerto No. 3 in D Minor Op. 30: III. Finale. Alla breve,1921,4,831667,80.954,-20.096,0.0594,0.982,0.279,0.211,0.665,0.0366,1" + System.lineSeparator() +
                "7xPhfUan2yNtyFG0cUWkt8,['Dennis Day'],Clancy Lowered the Boom,1922,5,180533,60.936,-12.441,0.963,0.732,0.819,0.341,0.16,0.415,0" + System.lineSeparator() +
                "1o6I8BglA6ylDMrIELygv1,['KHP Kridhamardawa Karaton Ngayogyakarta Hadiningrat'],Gati Bali,1922,5,500062,110.339,-14.85,0.0394,0.961,0.328,0.166,0.101,0.0339,0" + System.lineSeparator() +
                "0MJZ4hh60zwsYleWWxT5yW,['Zay Gatsby';'Dennis Day'],Power Is Power,1921,0,205072,159.935,-7.298,0.493,0.0175,0.527,0.691,0.358,0.0326,1" + System.lineSeparator() +
                "0dhibLK5uxmgX6wy4s04pE,['Cheo Feliciano'],Naborí,1990,32,258333,92.525,-14.675,0.916,0.833,0.659,0.456,0.0745,0.054,0" + System.lineSeparator() +
                "alobutalo,['Cheo Feliciano'],Naborí,1992,35,258333,92.525,-14.675,0.916,0.833,0.659,0.456,0.0745,0.054,0" + System.lineSeparator() +
                "valence1,['Cheo Feliciano'],Naborí,1987,35,258333,92.525,-14.675,1,0.833,0.659,0.456,0.0745,0.054,0" + System.lineSeparator() +
                "valence2,['Cheo Feliciano'],Naborí,1986,35,258333,92.525,-14.675,2,0.833,0.659,0.456,0.0745,0.054,0" + System.lineSeparator() +
                "valence3,['Cheo Feliciano'],Naborí,1987,35,258333,92.525,-14.675,3,0.833,0.659,0.456,0.0745,0.054,0";

        Reader datasetReader = new StringReader(dataset);
        testSpotifyExplorer = new SpotifyExplorer(datasetReader);
    }

    @Test
    public void testGetExplicitTracks() {
        Set<String> expectedIds = new HashSet<>();
        expectedIds.add("4BJqT0PrAfrxzMOxytFOIz");
        expectedIds.add("0MJZ4hh60zwsYleWWxT5yW");
        Collection<String> result = testSpotifyExplorer.getExplicitSpotifyTracks()
                .stream()
                .map(SpotifyTrack::id).collect(Collectors.toSet());

        assertEquals(expectedIds, result);
    }

    @Test
    public void testGroupByYear() {
        Set<String> year_1921 = new HashSet<>();
        year_1921.add("4BJqT0PrAfrxzMOxytFOIz");
        year_1921.add("0MJZ4hh60zwsYleWWxT5yW");

        Set<String> year_1922 = new HashSet<>();
        year_1922.add("7xPhfUan2yNtyFG0cUWkt8");
        year_1922.add("1o6I8BglA6ylDMrIELygv1");

        Map<Integer, Set<String>> expectedResult = new HashMap<>();
        expectedResult.put(1921, year_1921);
        expectedResult.put(1922, year_1922);

        Map<Integer, Set<SpotifyTrack>> result = testSpotifyExplorer.groupSpotifyTracksByYear();

        assertEquals(year_1921, result.get(1921)
                .stream()
                .map(SpotifyTrack::id)
                .collect(Collectors.toSet()));

        assertEquals(year_1922, result.get(1922)
                .stream()
                .map(SpotifyTrack::id)
                .collect(Collectors.toSet()));

    }

    @Test
    public void testGetActiveYears() {
        assertEquals(2, testSpotifyExplorer.getArtistActiveYears("Dennis Day"));
    }

    @Test
    public void testGetActiveYearsNoTracks() {
        assertEquals(0, testSpotifyExplorer.getArtistActiveYears("Stanislav Dimo"));
    }

    @Test
    public void testGetMostPopularTracks90s() {
        String expectedResult = "alobutalo";

        assertEquals(expectedResult, testSpotifyExplorer.getMostPopularTrackFromThe90s().id());
    }

    @Test
    public void testGetTopNHighestValenceTracks() {
        List<String> expectedResult = new LinkedList<>();
        expectedResult.add("valence3");
        expectedResult.add("valence2");

        List<String> result = testSpotifyExplorer.getTopNHighestValenceTracksFromThe80s(2).stream()
                .map(SpotifyTrack::id)
                .collect(Collectors.toUnmodifiableList());

        assertEquals(expectedResult, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTopNHighestValenceTracksNegative() {
        testSpotifyExplorer.getTopNHighestValenceTracksFromThe80s(-1);
    }
}
