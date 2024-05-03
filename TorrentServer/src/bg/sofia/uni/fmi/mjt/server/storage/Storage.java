package bg.sofia.uni.fmi.mjt.server.storage;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Storage {
    private final Map<String, UserInfo> activeUsersInfo;
    private final Map<String, Set<String>> userFiles;

    public Storage() {
        activeUsersInfo = new HashMap<>();
        userFiles = new HashMap<>();
    }

    public void connectUser(String user, UserInfo userInfo) {
        activeUsersInfo.put(user, userInfo);
    }

    public void disconnectUser(String user) {
        activeUsersInfo.remove(user);
    }

    public void registerFiles(String user, String[] files) {

        if (userFiles.containsKey(user)) {
            userFiles.get(user).addAll(Arrays.asList(files));
        } else {
            userFiles.put(user, new HashSet<>(Arrays.asList(files)));
        }
    }

    public void unregisterFiles(String user, String[] files) {
        if (userFiles.containsKey(user)) {
            userFiles.get(user).removeAll(Arrays.asList(files));
        }
    }

    public boolean isUserActive(String user) {
        return activeUsersInfo.containsKey(user);
    }

    public Map<String, Set<String>> getUserFiles() {
        return Collections.unmodifiableMap(userFiles);
    }

    public Map<String, UserInfo> getActiveUsers() {
        return Collections.unmodifiableMap(activeUsersInfo);
    }


}
