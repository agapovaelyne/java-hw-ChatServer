package server;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Logger LOGGER = Logger.getLogger(Server.class);
    protected final static String CONFIG = "src/main/resources/settings.conf";

    protected int PORT;
    protected int THREADS_NUMBER;
    protected String CLIENTS_IN_CHAT;
    protected List<ClientConnector> clients;
    protected final ExecutorService serverThreadPool;

    public Server() throws IOException {
        setSettings();
        LOGGER.debug("Server configuration has been set");
        serverThreadPool = Executors.newFixedThreadPool(THREADS_NUMBER);
        clients = new CopyOnWriteArrayList<>();
    }

    public void runServer() {
        Socket clientSocket;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            LOGGER.debug("Server is up");
            System.out.println("Server is up");
            while (true) {
                clientSocket = serverSocket.accept();
                ClientConnector client = new ClientConnector(clientSocket, this);
                LOGGER.debug(String.format("Client %s connected", clientSocket.getInetAddress().getHostAddress()));
                clients.add(client);
                LOGGER.debug("Client added to server clients list");
                serverThreadPool.submit(client);
                LOGGER.debug("New Server Thread has been started for client " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            LOGGER.debug("Server stopped");
            serverThreadPool.shutdown();
        }
    }

    private void setSettings() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(CONFIG));
        LOGGER.debug("Settings file has been loaded:" + CONFIG);
        PORT = Integer.parseInt(props.getProperty("PORT", "3443"));
        LOGGER.debug("Server PORT value has been set to " + PORT);
        THREADS_NUMBER = Integer.parseInt(props.getProperty("THREADS_NUMBER", "10"));
        LOGGER.debug("Server THREADS_NUMBER value has been set to " + THREADS_NUMBER);
        CLIENTS_IN_CHAT = props.getProperty("CLIENTS_IN_CHAT");
        LOGGER.debug("Server CLIENTS_IN_CHAT value has been set to " + CLIENTS_IN_CHAT);
    }

    protected void sendMessageToChat(String message) {
        for (ClientConnector client : clients) {
            client.sendMessage(message);
        }
        if (!message.contains(CLIENTS_IN_CHAT)) {
            String sender = message.contains(":") ? message.substring(0, message.indexOf(":")) : "Server";
            LOGGER.info(String.format("Message to chat from %s: '%s'", sender, message.substring(message.indexOf(":") + 1)));
        } else {
            LOGGER.debug(String.format("Service message (CLIENTS_IN_CHAT) was sent to clients: '%s'", message));
        }
    }

    protected void removeClient(ClientConnector client) {
        clients.remove(client);
        LOGGER.debug("Client removed from server clients list");
    }
}
