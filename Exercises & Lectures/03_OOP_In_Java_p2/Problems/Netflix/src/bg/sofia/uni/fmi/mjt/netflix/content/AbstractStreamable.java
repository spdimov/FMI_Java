package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;

public abstract class AbstractStreamable implements Streamable {
    String name;
    Genre genre;
    PgRating rating;
    long timesWatched;

    AbstractStreamable(String name, Genre genre, PgRating rating) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
        timesWatched = 0;
    }

    public void watched() {
        timesWatched++;
    }

    public long getTimesWatched() {
        return timesWatched;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public PgRating getRating() {
        return rating;
    }
}
