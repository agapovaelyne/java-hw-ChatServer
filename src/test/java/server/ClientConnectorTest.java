package server;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientConnectorTest {

    private static final String CONFIG = "src/main/resources/settings.conf";
    private static final Properties PROPS = new Properties();

    @Test
    public void clientsOnline_test() throws IOException {
        int startNumber = ClientConnector.getClientsOnline();
        int expected = startNumber + 1;
        new ClientConnector(new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG)), new Server());
        int actual = ClientConnector.getClientsOnline();
        assertEquals(expected, actual);
    }

    @Test
    public void ClientConnectorSettings_Test() throws IOException {
        ClientConnector connector = new ClientConnector(new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG)), new Server());
        assertEquals(connector.getUsernameCommand(), Helper.getUsernameCommandText(PROPS, CONFIG));
        assertEquals(connector.getExitCommand(), Helper.getExitCommandText(PROPS, CONFIG));
        assertEquals(connector.getClientsInChat(), Helper.getUsersOnlineCommand(PROPS, CONFIG));
        assertEquals(connector.getThreadSleepDiapason(), Helper.getThreadSleepDiapason(PROPS, CONFIG));
    }

}
