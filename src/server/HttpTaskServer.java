package server;

import com.sun.net.httpserver.HttpServer;
import handlers.EpicHandler;
import handlers.HistoryHandler;
import handlers.PrioritizedHandler;
import handlers.SubtaskHandler;
import handlers.TaskHandler;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

public class HttpTaskServer {

    Logger logger = Logger.getLogger(getClass().getName());

    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager manager) throws IOException {
        HistoryManager historyManager = manager.getHistoryManager();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        httpServer.setExecutor(null);
        httpServer.start();
        logger.info("Server started on port " + PORT);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            logger.info("Server stopped.");
        }
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefaultManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }
}