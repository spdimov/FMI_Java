package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Post extends AbstractContent {

    public Post(String username, LocalDateTime publishedOn, String description) {
        super(username, publishedOn, description);
    }

    @Override
    public boolean isExpired() {
        long daysFromPublish = ChronoUnit.DAYS.between(this.publishedOn, LocalDateTime.now());

        return daysFromPublish > 30;
    }
}
