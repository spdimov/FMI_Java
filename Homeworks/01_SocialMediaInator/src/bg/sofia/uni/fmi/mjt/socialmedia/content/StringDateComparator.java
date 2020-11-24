package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;


public class StringDateComparator implements Comparator<String> {
    private static final int DATE_END_INDEX = 17;

    @Override
    public int compare(String o1, String o2) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        LocalDateTime o1Time = LocalDateTime.parse(o1.substring(0, DATE_END_INDEX), formatter);
        LocalDateTime o2Time = LocalDateTime.parse(o2.substring(0, DATE_END_INDEX), formatter);

        return o1Time.isBefore(o2Time) ? 1 : 0;

    }
}
