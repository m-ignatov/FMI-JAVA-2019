package bg.sofia.uni.fmi.mjt.p2p.client;

import bg.sofia.uni.fmi.mjt.p2p.client.enums.Command;
import bg.sofia.uni.fmi.mjt.p2p.client.exceptions.ClientException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CommandRunner implements Runnable {

    private static final int BUFFER_SIZE = 8192;

    private PrintWriter writer;
    private Scanner scanner;
    private Client client;

    public CommandRunner(Client client, PrintWriter writer) {
        this.client = client;
        this.writer = writer;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();

            String[] commands = message.trim().split(" ");
            Command command = Command.get(commands[0]);

            if (!isValid(command, commands.length - 1)) {
                System.out.println("Unrecognised command");
                continue;
            }

            switch (command) {
                case DISCONNECT:
                    System.out.println("User disconnected");
                    writer.println(message);
                    return;
                case DOWNLOAD:
                    String file = download(message);
                    register(file);
                    break;
                case REGISTER:
                    writer.println(message + " " + client.getMiniServerAddress());
                    break;
                default:
                    writer.println(message);
            }
        }
    }

    private void register(String file) {
        if (file != null && client.getUser() != null) {
            writer.println(String.format("register %s %s", client.getUser(), file));
        }
    }

    private String download(String command) {
        if (client.getUserMappingsPath() == null) {
            System.out.println("User mappings not available yet. Please wait 30 sec.");
            return null;
        }

        String[] split = command.split(" ");
        String user = split[1];
        Path fileInputDir = Path.of(split[2]);
        Path fileName = fileInputDir.getFileName();
        Path outputDir = Path.of(split[3]);

        SocketAddress socketAddress = getSocketAddress(user, client.getUserMappingsPath());
        String fileOutputDir = outputDir + File.separator + fileName;

        if (!transferFile(socketAddress, fileInputDir.toString(), fileOutputDir)) {
            return null;
        }
        System.out.println(String.format("File %s downloaded successfully", fileOutputDir));
        return fileOutputDir;
    }

    /**
     * Transfer file from input directory to output directory.
     *
     * @param socketAddress target socket address
     * @param fileInputDir  input directory
     * @param fileOutputDir output directory
     * @return true if success; false otherwise
     */
    private boolean transferFile(SocketAddress socketAddress, String fileInputDir, String fileOutputDir) {
        try (SocketChannel socketChannel = SocketChannel.open();
             FileOutputStream out = new FileOutputStream(fileOutputDir);
             PrintWriter output = new PrintWriter(Channels.newWriter(socketChannel, UTF_8), true)) {

            socketChannel.connect(socketAddress);
            output.println(fileInputDir);

            int count;
            byte[] buffer = new byte[BUFFER_SIZE];

            InputStream in = socketChannel.socket().getInputStream();
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return false;
        } catch (IOException e) {
            throw new ClientException("Failed to receive file", e);
        }
        return true;
    }

    /**
     * Find socket address of target user.
     *
     * @param user         target user
     * @param userMappings file with user mappings
     * @return
     */
    private SocketAddress getSocketAddress(String user, Path userMappings) {
        try {
            List<String> userMapping = Files.readAllLines(userMappings);
            for (String entry : userMapping) {
                String[] pair = entry.split(" - ");
                if (pair[0].equals(user)) {
                    String address = pair[1];
                    return buildSocketAddress(address);
                }
            }
            return null;
        } catch (IOException e) {
            throw new ClientException("Failed reading from user mappings", e);
        }
    }

    /**
     * Returns a socket address from given string.
     *
     * @param targetAddress string
     * @return socket address
     */
    private SocketAddress buildSocketAddress(String targetAddress) {
        String host = targetAddress.substring(0, targetAddress.indexOf(':'));
        int port = Integer.parseInt(targetAddress.substring(targetAddress.indexOf(':') + 1));
        return new InetSocketAddress(host, port);
    }

    /**
     * @param command   target command
     * @param arguments number of arguments
     * @return True if the target command is valid (has valid number of arguments);
     * false otherwise
     */
    private boolean isValid(Command command, int arguments) {
        return (command != null) && (command.getArguments() == arguments);
    }
}