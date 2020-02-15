package bg.sofia.uni.fmi.mjt.p2p.server;

import bg.sofia.uni.fmi.mjt.p2p.server.enums.Command;
import bg.sofia.uni.fmi.mjt.p2p.server.exceptions.ServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.p2p.server.enums.Command.LIST_USERS;
import static bg.sofia.uni.fmi.mjt.p2p.server.enums.Command.REGISTER;
import static bg.sofia.uni.fmi.mjt.p2p.server.enums.Command.UNREGISTER;

public class RequestHandler {

    private Map<String, SocketAddress> users;
    private Map<String, List<Path>> files;

    public RequestHandler() {
        this.users = new HashMap<>();
        this.files = new HashMap<>();
    }

    /**
     * Handles all client requests.
     *
     * @param key     selection key
     * @param request client request
     * @return response
     */
    public String handleRequest(SelectionKey key, String request) {
        String[] commands = request.split(" ");
        Command command = Command.get(commands[0]);

        if (command == null) {
            throw new ServerException("Command is invalid");
        }

        String user = null;
        String[] userFiles = null;

        if (REGISTER.equals(command) || UNREGISTER.equals(command)) {
            if (REGISTER.equals(command) && key.attachment() == null) {
                String address = commands[commands.length - 1];
                attachAddressToKey(key, address);
            }
            user = commands[1];
            userFiles = commands[2].split(";");
        }

        SocketAddress address = (SocketAddress) key.attachment();
        switch (command) {
            case REGISTER:
                return registerUser(address, user, userFiles);
            case UNREGISTER:
                return unregisterUser(user, userFiles);
            case LIST_FILES:
                return listFiles();
            case LIST_USERS:
                String currentUser = findUser(address);
                if (currentUser != null) {
                    return listUsers(currentUser);
                }
                return null;
            case DISCONNECT:
                disconnect(key);
                return null;
            default:
                return "Something went wrong";
        }
    }

    private void attachAddressToKey(SelectionKey key, String currentAddress) {
        String host = currentAddress.substring(0, currentAddress.indexOf(':'));
        int port = Integer.parseInt(currentAddress.substring(currentAddress.indexOf(':') + 1));
        SocketAddress socketAddress = new InetSocketAddress(host, port);
        key.attach(socketAddress);
    }

    /**
     * Returns user list of the peers of given target user.
     * The list includes all active users in the system, excluding the target user.
     *
     * @param currentUser target user
     * @return user list
     */
    private String listUsers(String currentUser) {
        StringBuilder stringBuilder =
                new StringBuilder(String.format("%s,%s,", LIST_USERS.getName(), currentUser));
        int startLength = stringBuilder.length();

        users.forEach((user, address) -> {
            if (!user.equals(currentUser)) {
                stringBuilder.append(String.format("%s - %s,", user, address.toString().split("/")[1]));
            }
        });
        return (stringBuilder.length() > startLength) ? stringBuilder.toString() : null;
    }

    /**
     * Disconnects user by given socket channel and removes all his data.
     *
     * @param key selection key
     * @return null
     */
    private void disconnect(SelectionKey key) {
        SocketChannel userAddress = (SocketChannel) key.channel();
        SocketAddress miniServerAddress = (SocketAddress) key.attachment();
        String user = findUser(miniServerAddress);

        users.remove(user);
        files.remove(user);

        close(userAddress);
    }

    /**
     * Find registered user by given socket address.
     *
     * @param userAddress socket address
     * @return user; null, if not found
     */
    private String findUser(SocketAddress userAddress) {
        String user = null;
        for (Map.Entry<String, SocketAddress> entry : users.entrySet()) {
            SocketAddress address = entry.getValue();
            if (address.equals(userAddress)) {
                user = entry.getKey();
                break;
            }
        }
        return user;
    }

    /**
     * Attempts to close the given socket channel.
     *
     * @param channel socket channel
     */
    private void close(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            throw new ServerException("Cannot close socket channel");
        }
    }

    /**
     * Registers new user and his files.
     *
     * @param address   socket address of target user
     * @param user      target user
     * @param userFiles list of files to register
     * @return message result
     */
    private String registerUser(SocketAddress address, String user, String[] userFiles) {
        if (isUserAlreadyRegistered(user, address)) {
            return String.format("Username '%s' already registered", user);
        }
        users.put(user, address);
        addUserFiles(user, userFiles);
        return String.format("[%s] Files registered successfully", user);
    }

    /**
     * @param user       target user
     * @param newAddress socket address
     * @return True if the target user name already exists under another socket address,
     * different from the given one; false otherwise
     */
    private boolean isUserAlreadyRegistered(String user, SocketAddress newAddress) {
        SocketAddress currentAddress = users.get(user);
        return (currentAddress != null) && (!currentAddress.equals(newAddress));
    }

    /**
     * Adds given user files to target user system entry.
     *
     * @param user      target user
     * @param userFiles user files
     */
    private void addUserFiles(String user, String[] userFiles) {
        List<Path> currentFiles = files.getOrDefault(user, new ArrayList<>());
        for (String file : userFiles) {
            currentFiles.add(Path.of(file));
        }
        files.put(user, currentFiles);
    }

    /**
     * Unregisters user files.
     * If the target user has no files, it is also removed as entry.
     *
     * @param user              target user
     * @param filesToUnregister list of files to unregister
     * @return message result
     */
    private String unregisterUser(String user, String[] filesToUnregister) {
        List<Path> currentFiles = files.get(user);
        if (currentFiles == null) {
            return String.format("Username [%s] does not exist", user);
        }

        for (String file : filesToUnregister) {
            currentFiles.remove(Path.of(file));
        }
        if (currentFiles.isEmpty()) {
            users.remove(user);
            files.remove(user);
        }
        return String.format("[%s] Files unregistered successfully", user);
    }

    /**
     * @return list of all available files
     */
    private String listFiles() {
        StringBuilder builder = new StringBuilder();
        files.forEach((user, userFiles) ->
                userFiles.forEach(file -> builder.append(String.format("%s : %s%n", user, file))));
        return (builder.length() > 0) ? builder.toString() : "No registered files";
    }
}
