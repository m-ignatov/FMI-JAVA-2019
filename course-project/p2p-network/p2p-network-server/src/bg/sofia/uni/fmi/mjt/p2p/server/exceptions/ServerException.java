package bg.sofia.uni.fmi.mjt.p2p.server.exceptions;

public class ServerException extends RuntimeException {

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(String message) {
        super(message);
    }
}
