package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.Comparator;


public class StringDateComparator implements Comparator<String> {
    private final static int DATE_END_INDEX = 16;

    @Override
    public int compare(String o1, String o2) {

        LocalDateTime o1Time = LocalDateTime.parse(o1.substring(0, DATE_END_INDEX));
        LocalDateTime o2Time = LocalDateTime.parse(o2.substring(0, DATE_END_INDEX));

        return o1Time.isBefore(o2Time) ? 1 : 0;

    }
}
