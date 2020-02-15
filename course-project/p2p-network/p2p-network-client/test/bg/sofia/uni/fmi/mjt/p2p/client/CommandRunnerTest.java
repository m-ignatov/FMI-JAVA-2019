package bg.sofia.uni.fmi.mjt.p2p.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CommandRunnerTest {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private ByteArrayOutputStream outContent;
    private PrintWriter writer;
    private Client client;

    @Before
    public void setup() {
        client = mock(Client.class);

        outContent = new ByteArrayOutputStream();
        writer = new PrintWriter(outContent);

        System.setOut(new PrintStream(outContent));
    }

    @After
    public void cleanup() throws IOException {
        outContent.flush();

        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void givenInvalidCommandWhenCommandRunnerStartThenErrorMessage() {
        setInputText("invalid-command");

        new CommandRunner(client, writer).run();

        assertEquals("Unrecognised command", outContent.toString().trim());
    }

    @Test
    public void givenCommandWhenCommandRunnerStartThenWriteCommandToOutput() {
        String command = "register user file";
        setInputText(command);

        new CommandRunner(client, writer).run();
        writer.flush();

        assertEquals(command + " null", outContent.toString().trim());
    }

    @Test
    public void givenDisconnectCommandWhenCommandRunnerStartThenWriteCommandToOutput() {
        String command = "disconnect";
        setInputText(command);

        new CommandRunner(client, writer).run();
        writer.flush();

        String result = outContent.toString().trim();
        assertTrue(result.contains(command));
        assertTrue(result.contains("User disconnected"));
    }

    private void setInputText(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }
}
