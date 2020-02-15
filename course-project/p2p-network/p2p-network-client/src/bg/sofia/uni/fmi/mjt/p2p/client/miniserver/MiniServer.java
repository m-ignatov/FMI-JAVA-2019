package bg.sofia.uni.fmi.mjt.p2p.client.miniserver;

import bg.sofia.uni.fmi.mjt.p2p.client.Client;
import bg.sofia.uni.fmi.mjt.p2p.client.exceptions.ClientException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniServer implements Runnable {

    private static final int MAX_EXECUTOR_THREADS = 10;

    private ServerSocket serverSocket;
    private InetSocketAddress socketAddress;

    public MiniServer(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    @Override
    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try {
            serverSocket = new ServerSocket(socketAddress.getPort());
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DownloadRequestHandler clientHandler = new DownloadRequestHandler(clientSocket);
                executor.execute(clientHandler);
            }
        } catch (SocketException e) {
            System.out.println("Mini-server shut down");
        } catch (IOException e) {
            throw new ClientException("Mini-server IO error", e);
        } finally {
            executor.shutdown();
        }
    }

    public void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new ClientException("Cannot shutdown mini-server", e);
        }
    }
}

