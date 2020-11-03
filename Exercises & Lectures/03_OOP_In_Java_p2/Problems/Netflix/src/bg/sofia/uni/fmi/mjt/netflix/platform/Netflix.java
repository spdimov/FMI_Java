package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.AbstractStreamable;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;

public class Netflix implements StreamingService {
    Account[] accounts;
    Streamable[] streamableContent;

    public Netflix(Account[] accounts, Streamable[] streamableContent) {
        this.accounts = new Account[accounts.length];
        this.streamableContent = new Streamable[streamableContent.length];
        System.arraycopy(accounts, 0, this.accounts, 0, accounts.length);
        System.arraycopy(streamableContent, 0, this.streamableContent, 0, streamableContent.length);
    }

    @Override
    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        if (!Validation.isRegistered(user, accounts)) {
            throw new UserNotFoundException();
        }

        if (!Validation.isContentPresent(videoContentName, streamableContent)) {
            throw new ContentNotFoundException();
        }
        Streamable content = findByName(videoContentName);
        if (!Validation.isContentAvailable(content, user, streamableContent)) {
            throw new ContentUnavailableException();
        }
        if (content instanceof AbstractStreamable) {
            ((AbstractStreamable) content).watched();
        }
    }

    @Override
    public Streamable findByName(String videoContentName) {
        for (Streamable c : streamableContent) {
            if (c.getTitle().equals(videoContentName)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public Streamable mostViewed() {
        AbstractStreamable[] content = new AbstractStreamable[streamableContent.length];
        System.arraycopy(streamableContent, 0, content, 0, streamableContent.length);

        Streamable mostViewed = null;
        int mostTimesWatched = 0;
        for (AbstractStreamable c : content) {
            if (c.getTimesWatched() > mostTimesWatched) {
                mostViewed = c;
            }
        }
        return mostViewed;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        int totalTime = 0;
        AbstractStreamable[] content = new AbstractStreamable[streamableContent.length];
        System.arraycopy(streamableContent, 0, content, 0, streamableContent.length);

        for (AbstractStreamable c : content) {
            totalTime += c.getTimesWatched() * c.getDuration();
        }
        return totalTime;
    }
public static void main(String[] args){

}
}
