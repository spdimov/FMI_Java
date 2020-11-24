package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.content.AbstractContent;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.content.comparators.ByTimeComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.content.comparators.PopularityComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.content.comparators.StringDateComparator;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EvilSocialInator implements SocialMediaInator {
    public Map<String, AbstractContent> idToContent;
    Set<String> usersSet;
    Map<String, List<String>> userToActivityLog;

    public EvilSocialInator() {
        idToContent = new HashMap<>();
        usersSet = new HashSet<>();
        userToActivityLog = new HashMap<>();

    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException();
        }

        if (usersSet.contains(username)) {
            throw new UsernameAlreadyExistsException("User " + username + " already registered");
        } else {
            usersSet.add(username);
            userToActivityLog.put(username, new LinkedList<String>());
        }
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        Post post = new Post(username, publishedOn, description);
        publishContent(username, publishedOn, description, post);

        userToActivityLog.get(username).add(createPostLog(post.getId(), publishedOn));

        return post.getId();
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        Story story = new Story(username, publishedOn, description);
        publishContent(username, publishedOn, description, story);

        userToActivityLog.get(username).add(createStoryLog(story.getId(), publishedOn));

        return story.getId();
    }

    @Override
    public void like(String username, String id) {
        if (username == null || id == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (!usersSet.contains(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        if (!idToContent.containsKey(id)) {
            throw new ContentNotFoundException("Content with such id does not exist: " + id);
        }

        userToActivityLog.get(username).add(createLikeLog(id, LocalDateTime.now()));

        idToContent.get(id).addLike();

    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null || text == null || id == null) {
            throw new IllegalArgumentException("Null argument");
        }
        if (!usersSet.contains(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }
        if (!idToContent.containsKey(id)) {
            throw new ContentNotFoundException("Content with such id does not exist: " + id);
        }

        userToActivityLog.get(username).add(createCommentLog(id, text, LocalDateTime.now()));

        idToContent.get(id).addComment(text);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        removeExpired();

        List<Content> orderedByPopularityContent = new LinkedList<>();
        orderedByPopularityContent.addAll(idToContent.values());
        Collections.sort(orderedByPopularityContent, new PopularityComparator());

        if (orderedByPopularityContent.size() < n) {
            return Collections.unmodifiableCollection(orderedByPopularityContent);
        } else {
            return orderedByPopularityContent.subList(0, n);
        }
    }

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        removeExpired();

        List<Content> orderedByTimeUploadedContent = new LinkedList<>();

        for (AbstractContent content : idToContent.values()) {
            if (content.getUser().equals(username)) {
                orderedByTimeUploadedContent.add(content);
            }
        }

        Collections.sort(orderedByTimeUploadedContent, new ByTimeComparator());

        if (orderedByTimeUploadedContent.size() < n) {
            return Collections.unmodifiableCollection(orderedByTimeUploadedContent);
        } else {
            return orderedByTimeUploadedContent.subList(0, n);
        }
    }

    @Override
    public String getMostPopularUser() {
        if (usersSet.isEmpty()) {
            throw new NoUsersException("No users registered");
        }

        removeExpired();

        String mostPopularUser = "rew";
        int currentPopularityIndex = 0;
        int maxPopularityIndex = 0;

        for (String user : usersSet) {
            currentPopularityIndex = getUserPopularity(user);
            if (currentPopularityIndex > maxPopularityIndex) {
                maxPopularityIndex = currentPopularityIndex;
                mostPopularUser = user;
            }
        }

        return mostPopularUser;
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Null argument");
        }

        removeExpired();

        Set<Content> contentsByTag = new HashSet<>();

        for (Content content : idToContent.values()) {
            if (content.getTags().contains(tag)) {
                contentsByTag.add(content);
            }
        }

        return Collections.unmodifiableCollection(contentsByTag);
    }

    @Override
    public List<String> getActivityLog(String username) {
        List<String> logs = userToActivityLog.get(username);
        Collections.sort(logs, new StringDateComparator());
        return logs;
    }

    private void publishContent(String username, LocalDateTime publishedOn, String description, AbstractContent c) {
        if (username == null || publishedOn == null || description == null) {
            throw new IllegalArgumentException("Null argument");
        }

        if (!usersSet.contains(username)) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        idToContent.put(c.getId(), c);
    }

    private int getUserPopularity(String user) {
        int popularityIndex = 0;
        for (AbstractContent content : idToContent.values()) {
            popularityIndex += Collections.frequency(content.getMentions(), "@" + user);
        }

        return popularityIndex;
    }

    private String createCommentLog(String id, String text, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        String formatDateTime = time.format(formatter);
        return formatDateTime + ": Commented " + "\"" + text + "\" on a content with id " + id;
    }

    private String createLikeLog(String id, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        String formatDateTime = time.format(formatter);
        return formatDateTime + " Liked a content with id " + id;
    }

    private String createPostLog(String id, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        String formatDateTime = time.format(formatter);
        return formatDateTime + " Created a post with id " + id;
    }

    private String createStoryLog(String id, LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yy");
        String formatDateTime = time.format(formatter);
        return formatDateTime + " Created a story with id " + id;
    }

    private void removeExpired() {
        List<String> expired = new LinkedList<>();
        for (AbstractContent content : idToContent.values()) {
            if (content.isExpired()) {
                expired.add(content.getId());
            }
        }

        idToContent.keySet().removeAll(expired);
    }
}
