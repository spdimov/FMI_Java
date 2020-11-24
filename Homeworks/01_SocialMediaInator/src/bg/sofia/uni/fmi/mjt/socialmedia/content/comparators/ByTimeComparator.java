package bg.sofia.uni.fmi.mjt.socialmedia.content.comparators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.AbstractContent;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.Comparator;

public class ByTimeComparator implements Comparator<Content> {

    @Override
    public int compare(Content o1, Content o2) {
        if (!(o1 instanceof AbstractContent) || !(o2 instanceof AbstractContent)) {
            throw new IllegalArgumentException("Compared objects should be instance of AbstractContent");
        }

        return ((AbstractContent) o1).getPublishedOn().isAfter(((AbstractContent) o2).getPublishedOn()) ? 1 : 0;
    }
}
