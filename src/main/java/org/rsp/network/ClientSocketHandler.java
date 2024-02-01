package org.rsp.network;

import org.rsp.interaction.DialogInteractor;

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
class ClientSocketHandler implements Runnable {

    private final Logger logger = Logger.getLogger(ClientSocketHandler.class.getName());

    private final Socket socket;
    private final DialogInteractor interactor;

    ClientSocketHandler(Socket socket,
                        DialogInteractor interactor) {
        this.socket = socket;
        this.interactor = interactor;
    }

    @Override
    public void run() {
        try (var reader = new InputStreamReader(socket.getInputStream());
             var writer = new PrintWriter(socket.getOutputStream(), true)) {
            try {
                this.interactor.interact(new PlayerConnection(reader, writer));
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
