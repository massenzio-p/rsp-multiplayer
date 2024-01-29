package org.rsp;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import org.rsp.network.NonBlockingSocketHandler;
import org.rsp.network.SocketHandler;
import org.rsp.state.ServerStatus;
import org.rsp.util.ArgsParserUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.rsp.state.ServerStatus.RESOURCES_RELEASED;
import static org.rsp.state.ServerStatus.READY;

public class GameServer {
    private static final Logger logger = Logger.getLogger(GameServer.class.getName());
    private static final int CONNECTION_THREADS = 5;
    private static final int EVENT_BUS_THREADS = 5;
    static final int DEFAULT_PORT = 5050;

    private static AsyncEventBus eventBus;

    private final AtomicReference<ServerStatus> serverStatus = new AtomicReference<>(RESOURCES_RELEASED);
    private final int port;
    private final SocketHandler socketHandler;
    private ServerSocket serverSocket;

    GameServer(int port, SocketHandler socketHandler) {
        this.port = port;
        this.socketHandler = socketHandler;
    }


    public static void main(String[] args) {
        int port = ArgsParserUtils.determinePort(args, DEFAULT_PORT);

        eventBus = new AsyncEventBus(Executors.newFixedThreadPool(EVENT_BUS_THREADS));
        SocketHandler socketHandler =
                new NonBlockingSocketHandler(
                        Executors.newFixedThreadPool(CONNECTION_THREADS),
                        (r,w) -> {/** TODO: Replace this **/});

        GameServer gameServer = new GameServer(port, socketHandler);
        eventBus.register(gameServer);

        gameServer.startServer();
    }

    private static void reject(Socket socket, String msg) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(msg);
            socket.close();
            logger.info("Socket was rejected");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Something went wrong while rejecting the socket", e);
        }
    }

    void startServer() {
        try {
            initServer();
            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                switch (serverStatus.get()) {
                    case READY -> socketHandler.handleSocket(socket);
                    case TERMINATING -> reject(socket, "Sorry, but the server is terminating");
                    default -> {
                        socket.close();
                        throw new IllegalStateException("The serve have an unexpected state");
                    }
                }
            }
        } catch (IOException e) {
            logger.info( "Server shutdown");
        }
    }

    private void initServer() throws IOException {
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