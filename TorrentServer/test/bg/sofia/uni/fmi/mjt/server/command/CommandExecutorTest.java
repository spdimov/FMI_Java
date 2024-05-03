package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.storage.Storage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandExecutorTest {
    static Storage storage;
    static CommandExecutor commandExecutor;

    @Before
    public void clear() {
        storage = new Storage();
        commandExecutor = new CommandExecutor(storage);
    }

    @Test
    public void testConnectUserCorrect() {
        final String command = "connect";
        final String username = "vinica_boy";
        final String addressIP = "123";
        final String miniServerPort = "321";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, username, addressIP, miniServerPort));

        String expectedResponse = "Successfully connected";
        String response = commandExecutor.execute(cmd);

        assertTrue(storage.isUserActive(username));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testRegisterUserNotActiveFirstFile() {
        final String command = "register";
        final String username = "vinica_boy";
        final String file = "file";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, username, file));

        String expectedResponse = "Specified user should be active for this operation";
        String response = commandExecutor.execute(cmd);

        assertFalse(storage.getUserFiles().containsKey(username));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testRegisterUserActiveTest() {
        final String command = "register";
        final String username = "vinica_boy";
        final String file = "file";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, username, file));
        connectUser(username);

        String expectedResponse = "Successfully registered files";
        String response = commandExecutor.execute(cmd);

        assertTrue(storage.getUserFiles().get(username).contains(file));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testUnregisterActiveUser() {
        final String command = "unregister";
        final String username = "vinica_boy";
        final String file = "file";
        Command unregister = CommandCreator.newCommand(String.join(" ", command, username, file));
        Command register = CommandCreator.newCommand(String.join(" ", "register", username, file));
        connectUser(username);

        String expectedResponse = "Successfully unregistered files";
        commandExecutor.execute(register);
        String response = commandExecutor.execute(unregister);

        assertFalse(storage.getUserFiles().get(username).contains(file));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testListFilesEmpty() {
        final String command = "list-files";
        Command cmd = CommandCreator.newCommand(command);

        String expectedResponse = "";
        String response = commandExecutor.execute(cmd);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testListFilesOneFile() {
        final String command = "list-files";
        final String username = "vinica_boy";
        final String file = "file";
        Command register = CommandCreator.newCommand(String.join(" ", "register", username, file));
        Command listFiles = CommandCreator.newCommand(command);
        connectUser(username);

        String expectedResponse = "vinica_boy : file";
        commandExecutor.execute(register);
        String response = commandExecutor.execute(listFiles).replace("\\,", "");

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testUpdate() {
        final String command = "update";
        final String user = "vinica_boy";
        Command cmd = CommandCreator.newCommand(command);
        connectUser(user);

        String expectedResponse = "vinica_boy - 123:321";
        String response = commandExecutor.execute(cmd).replace("\\,", "");

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testDisconnectNotActiveUser() {
        final String command = "disconnect";
        final String user = "vinica_boy";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, user));

        String expectedResponse = "Specified user should be active for this operation";
        String response = commandExecutor.execute(cmd);

        assertFalse(storage.isUserActive(user));
        assertEquals(expectedResponse, response);
    }

    @Test
    public void testDisconnectActiveUser() {
        final String command = "disconnect";
        final String user = "vinica_boy";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, user));
        connectUser(user);

        String expectedResponse = "Successfully disconnected";
        String response = commandExecutor.execute(cmd);

        assertFalse(storage.isUserActive(user));
        assertEquals(expectedResponse, response);
    }

    private void connectUser(String user) {
        final String command = "connect";
        final String username = "vinica_boy";
        final String addressIP = "123";
        final String miniServerPort = "321";
        Command cmd = CommandCreator.newCommand(String.join(" ", command, username, addressIP, miniServerPort));

        commandExecutor.execute(cmd);
    }


}
