package org.rsp;

import com.google.common.eventbus.AsyncEventBus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rsp.network.SocketHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.rsp.state.ServerStatus.RESOURCES_RELEASED;
import static org.rsp.state.ServerStatus.TERMINATING;

class GameServerTest {

    private static final String HOST = "localhost";
    private final static ExecutorService executor = Executors.newSingleThreadExecutor();

    @Test
    void testIfServerStartsAndShutdowns() throws Exception {
        AsyncEventBus appEventBus = new AsyncEventBus(Executors.newFixedThreadPool(1));
        SocketHandler socketHandler = Mockito.mock(SocketHandler.class);
        Mockito.doAnswer(invocation -> {
                    Socket socket = invocation.getArgument(0);
                    return null;
                })
                .when(socketHandler)
                .handleSocket(Mockito.any());

        int port = 5858;
        GameServer gameServer = new GameServer(port, socketHandler);
        appEventBus.register(gameServer);

        Thread serverThread = new Thread(gameServer::startServer);
        serverThread.start();
        Thread.sleep(1000);


        Socket clientSocket = new Socket("localhost", port);
        Assertions.assertTrue(clientSocket.isConnected());
        clientSocket.close();
        Mockito.doAnswer(invocation -> {
                    Socket socket = invocation.getArgument(0);
                    socket.getInputStream().read();
                    return null;
                })
                .when(socketHandler)
                .handleSocket(Mockito.any());

        // test rejecting
        appEventBus.post(TERMINATING);
        Thread.sleep(1000);

        clientSocket = new Socket("localhost", port);
        Assertions.assertTrue(clientSocket.isConnected());
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        writer.write(1);
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Assertions.assertEquals("Sorry, but the server is terminating", reader.readLine());
        clientSocket.close();

        // test shutdown
        appEventBus.post(RESOURCES_RELEASED);
        Thread.sleep(1000);
        Assertions.assertThrows(ConnectException.class, () -> new Socket("localhost", port));
    }
}