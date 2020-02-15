package bg.sofia.uni.fmi.mjt.p2p.client;

import bg.sofia.uni.fmi.mjt.p2p.client.exceptions.ClientException;
import org.junit.Test;

public class ClientTest {

    private Client client = new Client();

    @Test(expected = ClientException.class)
    public void givenNoServerWhenRunClientThenThrowClientException() {
        client.run();
    }
}
