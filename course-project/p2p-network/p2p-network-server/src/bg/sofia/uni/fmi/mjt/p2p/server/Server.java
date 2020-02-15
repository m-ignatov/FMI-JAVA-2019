package bg.sofia.uni.fmi.mjt.p2p.server;

import bg.sofia.uni.fmi.mjt.p2p.server.exceptions.ServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Server {

    private static final String SERVER_HOST = "localhost";

    private static final int SERVER_PORT = 7777;
    private static final int BUFFER_SIZE = 1024;
    private static final int SLEEP_MILLIS = 200;

    private ByteBuffer buffer;
    private RequestHandler requestHandler;

    public Server() {
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        requestHandler = new RequestHandler();
    }

    public static void main(String[] args) {
        new Server().run();
    }

    public void run() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int readyChannels = selector.select();
                if (!hasReadyChannel(readyChannels)) {
                    continue;
                }
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                iterateKeys(selector, keyIterator);
            }
        } catch (IOException e) {
            throw new ServerException("There is a problem with the server socket", e);
        } finally {
            run();
        }
    }

    /**
     * @param readyChannels
     * @return True if there are any ready channels available; false otherwise
     */
    private boolean hasReadyChannel(int readyChannels) {
        if (readyChannels == 0) {
            System.out.println("Waiting for a ready channel...");
            try {
                Thread.sleep(SLEEP_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return false;
        }
        return true;
    }

    /**
     * Iterates over available selection keys
     */
    private void iterateKeys(Selector selector, Iterator<SelectionKey> keyIterator) throws IOException {
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                handleRequest(key);
            } else if (key.isAcceptable()) {
                registerClient(selector, key);
            }
            keyIterator.remove();
        }
    }

    /**
     * Registers a new client connection to the server.
     */
    private void registerClient(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = serverSocketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Handles client request.
     */
    private void handleRequest(SelectionKey selectionKey) throws IOException {
        SocketChannel client = (SocketChannel) selectionKey.channel();

        String request = read(client);
        if (request == null) {
            return;
        }
        String response = requestHandler.handleRequest(selectionKey, request);
        if (response != null) {
            write(client, response);
        }
    }

    /**
     * Reads data from the given socket channel.
     */
    private String read(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        int r = socketChannel.read(buffer);
        if (r <= 0) {
            System.out.println("Nothing to read, will close channel");
            socketChannel.close();
            return null;
        }
        return new String(buffer.array(), UTF_8).split("\n", 2)[0].trim();
    }

    /**
     * Writes a message to the given socket channel.
     */
    private void write(SocketChannel socketChannel, String message) throws IOException {
        if (message.charAt(message.length() - 1) != '\n') {
            message += '\n';
        }
        socketChannel.write(UTF_8.encode(message));
    }
}
