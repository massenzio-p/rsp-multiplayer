package org.rsp.network;

import com.google.common.eventbus.AsyncEventBus;
import org.rsp.interaction.DialogInteractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A non-blocking socket handler implementation.
 */
public class NonBlockingSocketHandler implements SocketHandler {

    private final Logger logger = Logger.getLogger(NonBlockingSocketHandler.class.getName());

    private final ExecutorService executorService;
    private final DialogInteractor interactor;

    public NonBlockingSocketHandler(ExecutorService executorService,
                                    DialogInteractor interactor) {
        this.executorService = executorService;
        this.interactor = interactor;
    }

    @Override
    public void handleSocket(Socket socket) {
        executorService.submit(() -> internalHandleSocketTask(socket));
    }

    private void internalHandleSocketTask(Socket socket) {
        try (var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var writer = new PrintWriter(socket.getOutputStream(), true)) {
            try {
                this.interactor.interact(reader, writer);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "An error occurred while interacting", e);
            }
            socket.close();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Something went wrong while handling the socket", e);
        }
        logger.info("Socket " + socket + " handled");
    }
}
