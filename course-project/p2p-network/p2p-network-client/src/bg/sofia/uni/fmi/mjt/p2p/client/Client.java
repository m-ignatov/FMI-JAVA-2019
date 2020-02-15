package bg.sofia.uni.fmi.mjt.p2p.client;

import bg.sofia.uni.fmi.mjt.p2p.client.exceptions.ClientException;
import bg.sofia.uni.fmi.mjt.p2p.client.miniserver.MiniServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static bg.sofia.uni.fmi.mjt.p2p.client.enums.Command.LIST_USERS;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Client {

    private static final int SERVER_PORT = 7777;
    private static final int PERIOD = 30;
    private static final int DELAY = 0;
    public static final String HOSTNAME = "localhost";

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private InetSocketAddress miniServerAddress;
    private Path userMappingsPath;
    private String user;
    private MiniServer miniServer;

    public static void main(String[] args) {
        new Client().run();
    }

    public void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, UTF_8), true)) {

            socketChannel.connect(new InetSocketAddress(HOSTNAME, SERVER_PORT));
            System.out.println("Connected to the server");

            startBackgroundJob(writer);
            startCommandRunner(writer);

            miniServerAddress = new InetSocketAddress(HOSTNAME, nextFreePort(49152, 65535));
            startMiniServer(miniServerAddress);

            String reply;
            while ((reply = reader.readLine()) != null) {
                handleReply(reply);
            }
        } catch (IOException e) {
            throw new ClientException("There is a problem with the client", e);
        } finally {
            scheduler.shutdown();
            miniServer.shutdown();
        }
    }

    /**
     * @param from start port
     * @param to   end port
     * @return free port number in the given range
     */
    public int nextFreePort(int from, int to) {
        int port = ThreadLocalRandom.current().nextInt(from, to);
        while (true) {
            if (isLocalPortFree(port)) {
                return port;
            }
            port = ThreadLocalRandom.current().nextInt(from, to);
        }
    }

    /**
     * @param port
     * @return true if given port is free; false otherwise
     */
    private boolean isLocalPortFree(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Starts background job to periodically fetch all available users.
     *
     * @param writer print writer
     */
    private void startBackgroundJob(PrintWriter writer) {
        Runnable task = () -> writer.println(LIST_USERS.getName());
        scheduler.scheduleAtFixedRate(task, DELAY, PERIOD, TimeUnit.SECONDS);
    }

    /**
     * Starts command runner for client input and processing.
     *
     * @param writer print writer
     */
    private void startCommandRunner(PrintWriter writer) {
        new Thread(new CommandRunner(this, writer)).start();
    }

    /**
     * Starts mini server for file transfer.
     *
     * @param address
     */
    private void startMiniServer(InetSocketAddress address) {
        miniServer = new MiniServer(address);
        new Thread(miniServer).start();
    }

    /**
     * Handles all server replies.
     *
     * @param reply server reply
     * @throws IOException
     */
    private void handleReply(String reply) throws IOException {
        String[] commands = reply.split(",", 3);

        if (LIST_USERS.getName().equals(commands[0])) {
            userMappingsPath = saveUserMappings(commands);
        } else if (commands[0].contains("Files registered successfully")) {
            System.out.println(reply);
            if (user == null) {
                user = extractUser(commands[0]);
            }
        } else {
            System.out.println(reply);
        }
    }

    private String extractUser(String command) {
        return command.substring(command.indexOf('[') + 1, command.indexOf(']'));
    }

    /**
     * Updates the user file consisting of all available users.
     *
     * @param commands
     * @return the path to the saved file
     * @throws IOException
     */
    private Path saveUserMappings(String[] commands) throws IOException {
        String currentUser = commands[1];
        String userMappings = commands[2].replace(",", "\n");

        Path base = Path.of("mappings" + File.separator + currentUser);
        Files.createDirectories(base);
        Path path = Path.of(base + File.separator + "users.txt");
        Files.write(path, userMappings.getBytes());

        return path;
    }

    public Path getUserMappingsPath() {
        return userMappingsPath;
    }

    public SocketAddress getMiniServerAddress() {
        return miniServerAddress;
    }

    public String getUser() {
        return user;
    }
}
