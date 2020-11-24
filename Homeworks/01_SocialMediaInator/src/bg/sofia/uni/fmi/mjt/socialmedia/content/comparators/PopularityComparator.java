package bg.sofia.uni.fmi.mjt.socialmedia.content.comparators;

import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;

import java.util.Comparator;

public final class PopularityComparator implements Comparator<Content> {
    @Override
    public int compare(Content o1, Content o2) {
        return (o2.getNumberOfComments() + o2.getNumberOfLikes())
                - (o1.getNumberOfComments() + o1.getNumberOfLikes());
    }
}
