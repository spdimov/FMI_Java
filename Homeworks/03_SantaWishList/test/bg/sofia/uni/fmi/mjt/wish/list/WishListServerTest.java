package bg.sofia.uni.fmi.mjt.wish.list;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.GIFT_SUBMITTED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_USERNAME;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.INVALID_USERNAME_PASSWORD;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_LOGGED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.NOT_REGISTERED_USER;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SAME_GIFT_SUBMITTED;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_LOGIN;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.SUCCESSFUL_REGISTRATION;
import static bg.sofia.uni.fmi.mjt.wish.list.MessageConstants.TAKEN_USERNAME;
import static org.junit.Assert.assertEquals;


public class WishListServerTest {
    WishListServer wishListServer;
    SocketChannel userSocketChannel;

    @Before
    public void setUp() throws IOException {
        final int SERVER_PORT = 5555;
        wishListServer = new WishListServer(SERVER_PORT);
        userSocketChannel = SocketChannel.open();
    }

    @After
    public void closeSocket() throws IOException {
        userSocketChannel.close();
    }

    @Test
    public void testRegisterInvalidUsername() throws IOException {
        String username = "Inval!d";
        String password = "password";
        String[] commandComponents = {"register", username, password};

        String response = wishListServer.register(commandComponents, userSocketChannel);
        String expectedResponse = String.format(INVALID_USERNAME, username);

        assertEquals(expectedResponse, response);
        userSocketChannel.close();
    }

    @Test
    public void testRegisterTakenUsername() {
        String username = "username";
        String password = "password";
        String[] commandComponents = {"register", username, password};
        wishListServer.registeredUsersToPassword.put(username, password);

        String response = wishListServer.register(commandComponents, userSocketChannel);
        String expectedResponse = String.format(TAKEN_USERNAME, username);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testRegisterValid() {
        String username = "username";
        String password = "password";
        String[] commandComponents = {"register", username, password};

        String response = wishListServer.register(commandComponents, userSocketChannel);
        String expectedResponse = String.format(SUCCESSFUL_REGISTRATION, username);

        assertEquals(expectedResponse, response);
    }

    @Test
    public void testLoginNotRegistered() {
        String username = "notRegisteredUsername";
        String password = "password";
        String[] commandComponents = {"login", username, password};

        String response = wishListServer.login(commandComponents, userSocketChannel);

        assertEquals(INVALID_USERNAME_PASSWORD, response);
    }

    @Test
    public void testLoginRegisteredWrongCombination() {
        String username = "RegisteredUsername";
        String password = "password";
        wishListServer.registeredUsersToPassword.put(username, password);
        String[] commandComponents = {"login", username, "wrongPassword"};

        String response = wishListServer.login(commandComponents, userSocketChannel);

        assertEquals(INVALID_USERNAME_PASSWORD, response);
    }

    @Test
    public void testLoginSuccessful() {
        String username = "username";
        String password = "password";
        wishListServer.registeredUsersToPassword.put(username, password);
        String[] commandComponents = {"login", username, "password"};

        String response = wishListServer.login(commandComponents, userSocketChannel);
        String expectedResult = String.format(SUCCESSFUL_LOGIN, username);

        assertEquals(expectedResult, response);
    }

    @Test
    public void testPostWishNotRegistered() {
        String username = "username";
        String present = "present";
        String[] commandComponents = {"post-wish", username, present};

        String response = wishListServer.postWish(commandComponents);
        String expectedResult = String.format(NOT_REGISTERED_USER, username);

        assertEquals(expectedResult, response);
    }

    @Test
    public void testPostWishSamePresent() {
        String username = "username";
        String password = "password";
        String present = "present";
        Set<String> presentsSet = new HashSet<>();
        presentsSet.add(present);
        wishListServer.usernameToPresents.put(username, presentsSet);
        wishListServer.registeredUsersToPassword.put(username, password);
        String[] commandComponents = {"post-wish", username, present};

        String response = wishListServer.postWish(commandComponents);
        String expectedResult = String.format(SAME_GIFT_SUBMITTED, username);

        assertEquals(expectedResult, response);
    }

    @Test
    public void testPostWishSuccessful() {
        String username = "username";
        String password = "password";
        String present = "present";
        Set<String> presentsSet = new HashSet<>();
        wishListServer.usernameToPresents.put(username, presentsSet);
        wishListServer.registeredUsersToPassword.put(username, password);
        String[] commandComponents = {"post-wish", username, present};

        String response = wishListServer.postWish(commandComponents);
        String expectedResult = String.format(GIFT_SUBMITTED, present, username);

        assertEquals(expectedResult, response);
    }

    @Test
    public void testGetWishDifferentUser() throws IOException {
        String username = "username";
        String present = "present";
        Set<String> presentsSet = new HashSet<>();
        presentsSet.add(present);
        wishListServer.usernameToPresents.put(username, presentsSet);
        wishListServer.channelToUsername.put(userSocketChannel, username);
        SocketChannel otherUserSocketChannel = SocketChannel.open();

        String response = wishListServer.getWish(otherUserSocketChannel);
        String expectedResult = "[ username: [present] ]";
        assertEquals(expectedResult, response);
    }

    @Test
    public void testNotLoggedIn() {
        String logout = "logout";
        String getWish = "get-wish";
        String postWish = "post-wish notlogged notlogged";

        String responseLogout = wishListServer.processCommand(logout, userSocketChannel);
        String responseGetWish = wishListServer.processCommand(getWish, userSocketChannel);
        String responsePostWish = wishListServer.processCommand(postWish, userSocketChannel);

        assertEquals(NOT_LOGGED, responseLogout);
        assertEquals(NOT_LOGGED, responseGetWish);
        assertEquals(NOT_LOGGED, responsePostWish);
    }

}
