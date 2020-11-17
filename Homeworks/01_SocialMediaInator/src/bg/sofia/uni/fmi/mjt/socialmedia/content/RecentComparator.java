package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.util.Comparator;

public class RecentComparator implements Comparator<AbstractContent> {

    @Override
    public int compare(AbstractContent o1, AbstractContent o2) {
        return o1.getPublishedOn().isAfter(o2.getPublishedOn()) ? 1 : 0;
    }
}
