package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractContent implements Content {
    protected static long identificatorNumber = 0;
    protected final LocalDateTime publishedOn;
    protected final String id;
    protected int likes;
    protected Set<String> comments;
    protected Set<String> tags;
    protected Set<String> mentions;

    public AbstractContent(String username, LocalDateTime publishedOn, String description) {
        id = username.concat("-").concat(String.valueOf(identificatorNumber));
        likes = 0;
        this.publishedOn = publishedOn;
        comments = new HashSet<>();
        tags = new HashSet<>();
        mentions = new HashSet<>();
        identificatorNumber++;
        extractTagsEndMentions(description);
    }

    @Override
    public int getNumberOfLikes() {
        return likes;
    }

    @Override
    public int getNumberOfComments() {
        return comments.size();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentions;
    }

    public void addLike() {
        likes++;
    }

    public void addComment(String text) {
        comments.add(text);
    }

    public abstract boolean isExpired();

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public String getUser() {
        return id.split("-")[0];
    }

    private void extractTagsEndMentions(String description) {
        String[] words = description.split("\\s+");
        for (String word : words) {
            if (word.matches("^#.+$")) {
                tags.add(word);
            } else if (word.matches("^@.+$")) {
                mentions.add(word);
            }
        }
    }
}
