package server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientConnector implements Runnable {
    private final Logger logger = Logger.getLogger(ClientConnector.class);

    private Server server;
    private PrintWriter messageOutput;
    private Scanner messageInput;
    private Socket clientSocket = null;
    private String clientsName = "New user";
    private final int threadSleepDiapason;
    private final String clientsInChat;
    private final String exitCommand;
    private final String usernameCommand;

    private static AtomicInteger clientsOnline = new AtomicInteger(0);
    private static final String USER_ENTERS_MESSAGE = " enters the chat!";
    private static final String USER_EXITS_MESSAGE = " has gone!";

    protected ClientConnector(Socket clientSocket, Server server) throws IOException {
        clientsInChat = server.getConfig().getClientsInChat();
        exitCommand = server.getConfig().getExitCommand();
        usernameCommand = server.getConfig().getUsernameCommand();
        threadSleepDiapason = server.getConfig().getThreadSleepDiapason();
        logger.debug("ClientConnector configuration has been set");
        try {
            logger.debug("Clients online: " + clientsOnline.incrementAndGet());
            this.server = server;
            this.clientSocket = clientSocket;
            messageOutput = new PrintWriter(clientSocket.getOutputStream());
            messageInput = new Scanner(clientSocket.getInputStream());
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                server.sendMessageToChat(clientsInChat + clientsOnline.get());
                break;
            }

            while (true) {
                if (messageInput.hasNext()) {
                    String clientMessage = messageInput.nextLine();
                    boolean isServiceMessageToLog = false;
                    if (clientMessage.startsWith(usernameCommand)) {
                        isServiceMessageToLog = true;
                        clientsName = clientMessage.substring(clientMessage.indexOf(usernameCommand) + usernameCommand.length());
                        clientMessage = clientsName + USER_ENTERS_MESSAGE;
                        logger.debug(String.format("Client %s introduced as '%s'", clientSocket.getInetAddress().getHostAddress(), clientsName));
                    }

                    if (clientMessage.equalsIgnoreCase(exitCommand)) {
                        isServiceMessageToLog = true;
                        server.sendMessageToChat(clientsName + USER_EXITS_MESSAGE);
                        logger.debug(String.format("Exit command has been received from client %s: '%s'", clientSocket.getInetAddress().getHostAddress(), clientMessage));
                        break;
                    }

                    if (!isServiceMessageToLog) {
                        logger.debug(String.format("Message has been received from %s%s", clientSocket.getInetAddress().getHostAddress(), !clientsName.equals("New user") ? " (" + clientsName + ")" : ""));
                    }

                    server.sendMessageToChat(clientMessage);
                }

                Thread.sleep(threadSleepDiapason);
            }
        } catch (InterruptedException e) {
            logger.error(e);
        } finally {
            this.close();
            logger.debug("Client connection closed for " + clientSocket.getInetAddress().getHostAddress());
        }
    }

    protected void sendMessage(String message) {
        try {
            messageOutput.println(message);
            messageOutput.flush();
            logger.debug(String.format("The message from server has been sent to client %s: '%s'", clientSocket.getInetAddress().getHostAddress(), message));
        } catch (Exception e) {
            logger.error(e);
        }
    }

    protected void close() {
        messageInput.close();
        server.removeClient(this);
        server.sendMessageToChat(clientsInChat + clientsOnline.decrementAndGet());
    }

    public int getThreadSleepDiapason() {
        return threadSleepDiapason;
    }

    public static int getClientsOnline() {
        return clientsOnline.get();
    }

    public static String getUserEntersMessage() {
        return USER_ENTERS_MESSAGE;
    }

    public static String getUserExitedMessage() {
        return USER_EXITS_MESSAGE;
    }

    public String getClientsInChat() {
        return clientsInChat;
    }

    public String getExitCommand() {
        return exitCommand;
    }

    public String getUsernameCommand() {
        return usernameCommand;
    }
}
