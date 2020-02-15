package bg.sofia.uni.fmi.mjt.p2p.client.miniserver;

import bg.sofia.uni.fmi.mjt.p2p.client.exceptions.ClientException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class DownloadRequestHandler implements Runnable {

    private static final int BUFFER_SIZE = 16 * 1024;
    private Socket socket;

    public DownloadRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String filePath = in.readLine().trim();

            File file = new File(filePath);
            try (InputStream input = new FileInputStream(file);
                 OutputStream out = socket.getOutputStream()) {

                int count;
                byte[] bytes = new byte[BUFFER_SIZE];

                while ((count = input.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            }
        } catch (IOException e) {
            throw new ClientException("Error with download socket", e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }
    }
}
