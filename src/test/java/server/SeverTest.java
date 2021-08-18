package server;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeverTest {

    private static final String CONFIG = "src/main/resources/settings.conf";
    private static final Properties PROPS = new Properties();

    @Test
    public void ServerIsUp_test() {
        boolean isConnected = false;
        try (Socket clientSocket = new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG))) {
            isConnected = clientSocket.isConnected();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assertTrue(isConnected);
    }

    @Test
    public void ServerSettings_test() throws IOException {
        Server server = new Server();
        assertEquals(server.getPort(), Helper.getPort(PROPS, CONFIG));
        assertEquals(server.getThreadsNumber(), Helper.getThreadsNumber(PROPS, CONFIG));
        assertEquals(server.getClientsInChat(), Helper.getUsersOnlineCommand(PROPS, CONFIG));
        assertEquals(server.getClients().size(), 0);
    }

    @Test
    public void usersOnlineInfoHasBeenReceived_test() throws IOException {
        String actual = null;
        try (Socket clientSocket = new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG))) {
            Scanner messageInput = new Scanner(clientSocket.getInputStream());

            if (messageInput.hasNext()) {
                actual = messageInput.nextLine();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assert actual != null;
        assertTrue(actual.contains(Helper.getUsersOnlineCommand(PROPS, CONFIG)));
    }

    @Test
    public void messageHasBeenSent_test() {
        String actual = null;
        String expected = "Test message";
        try (Socket clientSocket = new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG))) {
            Scanner messageInput = new Scanner(clientSocket.getInputStream());
            PrintWriter messageOutput = new PrintWriter(clientSocket.getOutputStream());

            if (messageInput.hasNext()) {
                messageInput.nextLine();
            }

            Thread.sleep(1000);
            messageOutput.println(expected);
            messageOutput.flush();

            if (messageInput.hasNext()) {
                actual = messageInput.nextLine();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void usernameCommand_test() throws IOException {
        String USERNAME_COMMAND = Helper.getUsernameCommandText(PROPS, CONFIG);
        String username = "Test user";
        String expected = username + ClientConnector.getUserEntersMessage();
        String actual = null;
        try (Socket clientSocket = new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG))) {
            Scanner messageInput = new Scanner(clientSocket.getInputStream());
            PrintWriter messageOutput = new PrintWriter(clientSocket.getOutputStream());

            if (messageInput.hasNext()) {
                messageInput.nextLine();
            }
            messageOutput.println(USERNAME_COMMAND + username);
            messageOutput.flush();

            if (messageInput.hasNext()) {
                actual = messageInput.nextLine();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assertEquals(expected, actual);
    }

    @Test
    public void exitCommand_test() throws IOException {
        String EXIT_COMMAND = Helper.getExitCommandText(PROPS, CONFIG);
        String exitTextMessagePart = ClientConnector.getUserExitedMessage();
        String serverOutput = null;
        try (Socket clientSocket = new Socket(Helper.getHost(PROPS, CONFIG), Helper.getPort(PROPS, CONFIG))) {
            Scanner messageInput = new Scanner(clientSocket.getInputStream());
            PrintWriter messageOutput = new PrintWriter(clientSocket.getOutputStream());

            if (messageInput.hasNext()) {
                messageInput.nextLine();
            }

            messageOutput.println(EXIT_COMMAND);
            messageOutput.flush();

            serverOutput = messageInput.nextLine();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        assert serverOutput != null;
        assertTrue(serverOutput.contains(exitTextMessagePart));
    }

    @Test
    public void setSettingsValuesTest_PORT() throws IOException {
        PROPS.load(new FileInputStream(CONFIG));
        int expected = 3443;
        int actual = Integer.parseInt(PROPS.getProperty("PORT", "0"));
        assertEquals(expected, actual);
    }

    @Test
    public void setSettingsValuesTest_THREADS_NUMBER() throws IOException {
        PROPS.load(new FileInputStream(CONFIG));
        int expected = 10;
        int actual = Integer.parseInt(PROPS.getProperty("THREADS_NUMBER", "0"));
        assertEquals(expected, actual);
    }

    @Test
    public void setSettingsValuesTest_CLIENTS_IN_CHAT() throws IOException {
        PROPS.load(new FileInputStream(CONFIG));
        String expected = "/users_online:";
        String actual = PROPS.getProperty("CLIENTS_IN_CHAT", null);
        assertEquals(expected, actual);
    }
}
