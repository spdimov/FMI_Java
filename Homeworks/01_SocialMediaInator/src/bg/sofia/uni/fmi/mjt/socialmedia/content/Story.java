package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Story extends AbstractContent {
    public Story(String username, LocalDateTime publishedOn, String descripton) {
        super(username, publishedOn, descripton);
    }

    @Override
    public boolean isExpired() {
        long hoursFromPublish = ChronoUnit.HOURS.between(this.publishedOn, LocalDateTime.now());

        return hoursFromPublish > 24;
    }


}
