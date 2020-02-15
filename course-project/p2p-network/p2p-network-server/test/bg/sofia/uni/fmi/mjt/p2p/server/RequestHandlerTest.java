package bg.sofia.uni.fmi.mjt.p2p.server;

import bg.sofia.uni.fmi.mjt.p2p.server.exceptions.ServerException;
import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SelectionKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class RequestHandlerTest {

    private static final String REGISTER_COMMAND = "register";
    private static final String UNREGISTER_COMMAND = "unregister";
    private static final String USER = "user";
    private static final String FILE = "file";
    private static final String ADDRESS = "127.0.0.1:50000";

    private RequestHandler handler;
    private SelectionKey key;

    @Before
    public void setup() {
        handler = new RequestHandler();

        key = mock(SelectionKey.class);
    }

    @Test(expected = ServerException.class)
    public void givenInvalidCommandWhenHandlingRequestThenThrowServerException() {
        handler.handleRequest(key, "invalid-command");
    }

    @Test
    public void givenNewUserWhenRegisterUserThenReturnSuccessfulMessage() {
        String response = handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, USER, FILE, ADDRESS));

        assertEquals(String.format("[%s] Files registered successfully", USER), response);
    }

    @Test
    public void givenExistingUserWhenRegisterUserWithDifferentAddressThenReturnUserAlreadyExistsMessage() {
        handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, USER, FILE, ADDRESS));

        String response = handler.handleRequest(mock(SelectionKey.class), String.format("%s %s %s 127.0.0.1:40001", REGISTER_COMMAND, USER, FILE));

        assertEquals(String.format("Username '%s' already registered", USER), response);
    }

    @Test
    public void givenExistingUserWhenUnregisterUserThenReturnSuccessfulMessage() {
        handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, USER, FILE, ADDRESS));
        String response = handler.handleRequest(key, String.format("%s %s %s", UNREGISTER_COMMAND, USER, FILE));

        assertEquals(String.format("[%s] Files unregistered successfully", USER), response);
    }

    @Test
    public void givenNonExistingUserWhenUnregisterUserThenReturnUserNotExistsMessage() {
        String response = handler.handleRequest(key, String.format("%s %s %s", UNREGISTER_COMMAND, USER, FILE));

        assertEquals(String.format("Username [%s] does not exist", USER), response);
    }

    @Test
    public void givenRegisteredFilesWhenListFilesThenReturnFiles() {
        handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, USER, FILE, ADDRESS));

        String response = handler.handleRequest(key, "list-files");

        assertEquals(String.format("%s : %s", USER, FILE), response.trim());
    }

    @Test
    public void givenNoRegisteredFilesWhenListFilesThenReturnNoUsersMessage() {
        String response = handler.handleRequest(key, "list-files");

        assertEquals("No registered files", response.trim());
    }

    @Test
    public void givenRegisteredUsersWhenListUsersThenReturnUsers() {
        handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, USER, FILE, ADDRESS));

        String fileNew = FILE + "-2";
        String userNew = USER + "-2";
        String addressNew = "127.0.0.1:50001";
        handler.handleRequest(key, String.format("%s %s %s %s", REGISTER_COMMAND, userNew, fileNew, addressNew));

        String response = handler.handleRequest(key, "list-users");

        String[] fields = response.split(",");

        assertEquals("list-users", fields[0]);
        assertEquals(userNew, fields[1]);
        assertEquals(String.format("%s - %s", USER, ADDRESS), fields[2]);
    }

    @Test
    public void givenNoRegisteredUsersWhenListUsersThenReturnNull() {
        String response = handler.handleRequest(key, "list-users");

        assertNull(response);
    }
}
