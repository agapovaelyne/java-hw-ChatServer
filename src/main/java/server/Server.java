package server;

import org.apache.log4j.Logger;
import server.config.Configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final Configuration config;
    private final int port;
    private final int threadsNumber;
    private final String clientsInChat;
    private final ExecutorService serverThreadPool;
    private final Logger logger = Logger.getLogger(Server.class);
    private List<ClientConnector> clients;

    public Server() throws IOException {
        config = new Configuration();
        port = config.getPort();
        threadsNumber = config.getThreadsNumber();
        clientsInChat = config.getClientsInChat();
        logger.debug("Server configuration has been set");
        serverThreadPool = Executors.newFixedThreadPool(threadsNumber);
        clients = new CopyOnWriteArrayList<>();
    }

    public void runServer() {
        Socket clientSocket;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.debug("Server is up");
            while (true) {
                clientSocket = serverSocket.accept();
                ClientConnector client = new ClientConnector(clientSocket, this);
                logger.debug(String.format("Client %s connected", clientSocket.getInetAddress().getHostAddress()));
                clients.add(client);
                serverThreadPool.submit(client);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            logger.debug("Server stopped");
            serverThreadPool.shutdown();
        }
    }

    protected void sendMessageToChat(String message) {
        for (ClientConnector client : clients) {
            client.sendMessage(message);
        }
        if (!message.contains(clientsInChat)) {
            String sender = message.contains(":") ? message.substring(0, message.indexOf(":")) : "Server";
            logger.info(String.format("Message to chat from %s: '%s'", sender, message.substring(message.indexOf(":") + 1)));
        }
    }

    protected void removeClient(ClientConnector client) {
        clients.remove(client);
    }

    public int getPort() {
        return port;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public String getClientsInChat() {
        return clientsInChat;
    }

    public List<ClientConnector> getClients() {
        return clients;
    }

    public Configuration getConfig() {
        return config;
    }
}
