package org.rsp;

import com.google.common.eventbus.Subscribe;
import org.rsp.interaction.DialogInteractorFactoryImpl;
import org.rsp.network.SocketHandlerFactory;
import org.rsp.network.SocketHandlerFactoryImpl;
import org.rsp.state.ContextHolder;
import org.rsp.state.ServerStatus;
import org.rsp.util.ArgsParserUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.rsp.state.ServerStatus.RESOURCES_RELEASED;
import static org.rsp.state.ServerStatus.READY;

public class GameServer {
    private static final Logger logger = Logger.getLogger(GameServer.class.getName());
    private static final int CONNECTION_THREADS = 5;
    static final int DEFAULT_PORT = 5050;


    private final AtomicReference<ServerStatus> serverStatus = new AtomicReference<>(RESOURCES_RELEASED);
    private final int port;
    private final SocketHandlerFactory socketHandlerFactory;
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    GameServer(int port, SocketHandlerFactory socketHandlerFactory) {
        this.port = port;
        this.socketHandlerFactory = socketHandlerFactory;
        // subscribe on the event bus and start server execution
        ContextHolder.eventBus().register(this);
    }

    public static void main(String[] args) {
        // parse the port parameter or default
        int port = ArgsParserUtils.determinePort(args, DEFAULT_PORT);

        // create an application event bus and factories
        SocketHandlerFactory socketHandlerFactory = new SocketHandlerFactoryImpl(
                new DialogInteractorFactoryImpl(
                        ContextHolder.getSessionRegistry(),
                        ContextHolder.getQueueRegistry(),
                        ContextHolder.getGameRegistry())
        );
        // Create and start a server
        GameServer gameServer = new GameServer(port, socketHandlerFactory);
        gameServer.startServer();
    }

    void startServer() {
        try {
            initServer();
            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                switch (serverStatus.get()) {
                    case READY -> executorService.submit(this.socketHandlerFactory.createSocketHandler(socket));
                    case TERMINATING -> executorService.submit(rejectRunnable(socket));
                    default -> {
                        socket.close();
                        throw new IllegalStateException("The serve have an unexpected state");
                    }
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            logger.info("Server shutdown");
        }
    }

    private Runnable rejectRunnable(Socket socket) {
        return () -> {
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("Sorry, but the server is terminating");
                socket.close();
                logger.info("Socket was rejected");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Something went wrong while rejecting the socket", e);
            }
        };
    }

    private void initServer() throws IOException {
        this.executorService = Executors.newFixedThreadPool(CONNECTION_THREADS);
        this.serverSocket = new ServerSocket(port);
        this.serverStatus.set(READY);
        logger.info(String.format("The server started on port %d", port));
    }

    public void tearDown() {
        if (this.serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error occurred while shutting down", e);
            }
        }
    }

    @Subscribe
    public void handleStateChange(ServerStatus status) {
        this.serverStatus.set(status);
        logger.info("The server changed the status to " + status);
        if (status == RESOURCES_RELEASED) {
            this.tearDown();
        }
    }
}