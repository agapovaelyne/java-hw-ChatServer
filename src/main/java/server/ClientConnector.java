package server;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class ClientConnector implements Runnable {
    protected final Logger LOGGER = Logger.getLogger(ClientConnector.class);

    protected Server server;
    protected PrintWriter messageOutput;
    protected Scanner messageInput;
    protected Socket clientSocket = null;
    protected String clientsName = "New user";
    protected int THREAD_SLEEP_DIAPASON;

    protected static int clientsOnline = 0;
    protected String CLIENTS_IN_CHAT;
    protected String EXIT_COMMAND;
    protected String USERNAME_COMMAND;
    protected static String userEntersMessage = " enters the chat!";
    protected static String userExitedMessage = " has gone!";

    protected ClientConnector(Socket clientSocket, Server server) throws IOException {
        LOGGER.debug(String.format("Client %s is serving by server", clientSocket.getInetAddress().getHostAddress()));
        getSettings();
        LOGGER.debug("ClientConnector configuration has been set");
        try {
            clientsOnline++;
            LOGGER.debug("Clients online: " + clientsOnline);
            System.out.println("Clients online: " + clientsOnline);
            this.server = server;
            this.clientSocket = clientSocket;
            messageOutput = new PrintWriter(clientSocket.getOutputStream());
            messageInput = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToChat(CLIENTS_IN_CHAT + clientsOnline);
                LOGGER.debug(String.format("Service message (CLIENTS_IN_CHAT) was sent to clients: '%s'", CLIENTS_IN_CHAT + clientsOnline));
                break;
            }

            while (true) {
                if (messageInput.hasNext()) {
                    String clientMessage = messageInput.nextLine();
                    boolean isServiceMessageToLog = false;
                    if (clientMessage.startsWith(USERNAME_COMMAND)) {
                        isServiceMessageToLog = true;
                        LOGGER.debug(String.format("Service message (USERNAME_COMMAND) has been received from client %s: '%s'", clientSocket.getInetAddress().getHostAddress(), clientMessage));
                        clientsName = clientMessage.substring(clientMessage.indexOf(USERNAME_COMMAND) + USERNAME_COMMAND.length());
                        clientMessage = clientsName + userEntersMessage;
                        LOGGER.debug(String.format("The client %s introduced as '%s'", clientSocket.getInetAddress().getHostAddress(), clientsName));
                    }

                    if (clientMessage.equalsIgnoreCase(EXIT_COMMAND)) {
                        isServiceMessageToLog = true;
                        server.sendMessageToChat(clientsName + userExitedMessage);
                        LOGGER.debug(String.format("Service message (EXIT_COMMAND) has been received from client %s: '%s'", clientSocket.getInetAddress().getHostAddress(), clientMessage));
                        break;
                    }

                    if (!isServiceMessageToLog) {
                        LOGGER.debug(String.format("Message has been received from %s%s", clientSocket.getInetAddress().getHostAddress(), !clientsName.equals("New user") ? " (" + clientsName + ")" : ""));
                    }

                    server.sendMessageToChat(clientMessage);
                    LOGGER.debug(String.format("The message from %s has been sent to the server: '%s'", clientSocket.getInetAddress().getHostAddress(), clientMessage));
                }

                Thread.sleep(THREAD_SLEEP_DIAPASON);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } finally {
            this.close();
            LOGGER.debug("Client service closed for " + clientSocket.getInetAddress().getHostAddress());
        }
    }

    private void getSettings() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(Server.CONFIG));
        LOGGER.debug("Settings file has been loaded:" + Server.CONFIG);
        CLIENTS_IN_CHAT = props.getProperty("CLIENTS_IN_CHAT");
        LOGGER.debug("ClientConnector CLIENTS_IN_CHAT value has been set to " + CLIENTS_IN_CHAT);
        EXIT_COMMAND = props.getProperty("EXIT_COMMAND");
        LOGGER.debug("ClientConnector EXIT_COMMAND value has been set to " + EXIT_COMMAND);
        USERNAME_COMMAND = props.getProperty("USERNAME_COMMAND");
        LOGGER.debug("ClientConnector USERNAME_COMMAND value has been set to " + USERNAME_COMMAND);
        THREAD_SLEEP_DIAPASON = Integer.parseInt(props.getProperty("THREAD_SLEEP_DIAPASON", "100"));
        LOGGER.debug("Server THREAD_SLEEP_DIAPASON value has been set to " + THREAD_SLEEP_DIAPASON);
    }

    protected void sendMessage(String message) {
        try {
            messageOutput.println(message);
            messageOutput.flush();
            LOGGER.debug(String.format("The message from server has been sent to client %s: '%s'", clientSocket.getInetAddress().getHostAddress(), message));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    protected void close() {
        messageInput.close();
        server.removeClient(this);
        LOGGER.debug("Close serving command has been sent to server for client " + clientSocket.getInetAddress().getHostAddress());
        clientsOnline--;
        LOGGER.debug("Clients online: " + clientsOnline);
        server.sendMessageToChat(CLIENTS_IN_CHAT + clientsOnline);
        LOGGER.debug(String.format("Service message (CLIENTS_IN_CHAT) was sent to clients: '%s'", CLIENTS_IN_CHAT + clientsOnline));
    }

}
