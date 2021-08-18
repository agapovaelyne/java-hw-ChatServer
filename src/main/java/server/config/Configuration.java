package server.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final String CONFIG = "src/main/resources/settings.conf";

    private final int port;
    private final int threadsNumber;
    private final int threadSleepDiapason;
    private final String clientsInChat;
    private final String exitCommand;
    private final String usernameCommand;

    public Configuration() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(CONFIG));

        port = Integer.parseInt(props.getProperty("PORT", "3443"));
        threadsNumber = Integer.parseInt(props.getProperty("THREADS_NUMBER", "10"));
        clientsInChat = props.getProperty("CLIENTS_IN_CHAT");
        exitCommand = props.getProperty("EXIT_COMMAND");
        usernameCommand = props.getProperty("USERNAME_COMMAND");
        threadSleepDiapason = Integer.parseInt(props.getProperty("THREAD_SLEEP_DIAPASON", "100"));
    }

    public int getPort() {
        return port;
    }

    public int getThreadsNumber() {
        return threadsNumber;
    }

    public int getThreadSleepDiapason() {
        return threadSleepDiapason;
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
