package server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientConnectorTest {

    private static final String CONFIG = Server.CONFIG;
    private static final Properties PROPS = new Properties();

    @Test
    public void clientsOnline_test() throws IOException {
        int startNumber = ClientConnector.clientsOnline;
        int expected = startNumber + 1;
        new ClientConnector(new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG)), new Server());
        int actual = ClientConnector.clientsOnline;
        assertEquals(expected, actual);
    }

    @Test
    public void ClientConnectorSettings_Test() throws IOException {
        ClientConnector connector = new ClientConnector(new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG)), new Server());
        assertEquals(connector.USERNAME_COMMAND, Helper.getUsernameCommandText(PROPS, CONFIG));
        assertEquals(connector.EXIT_COMMAND, Helper.getExitCommandText(PROPS, CONFIG));
        assertEquals(connector.CLIENTS_IN_CHAT, Helper.getUsersOnlineCommand(PROPS, CONFIG));
        assertEquals(connector.THREAD_SLEEP_DIAPASON, Helper.getThreadSleepDiapason(PROPS, CONFIG));
    }

}
