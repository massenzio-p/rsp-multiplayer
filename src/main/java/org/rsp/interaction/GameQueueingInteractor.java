package org.rsp.interaction;

import org.rsp.exception.TerminationException;
import org.rsp.network.Connection;
import org.rsp.network.session.Session;
import org.rsp.registry.queue.GameTicket;
import org.rsp.registry.queue.PlayerQueueRegistry;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameQueueingInteractor extends AbstractDialogInteractor implements DialogInteractor, SessionDialogInteractor {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final DialogInteractor nextInteractor;
    private final PlayerQueueRegistry playerQueueRegistry;

    private Session session;

    public GameQueueingInteractor(DialogInteractor interactor, PlayerQueueRegistry playerQueueRegistry) {
        this.nextInteractor = interactor;
        this.playerQueueRegistry = playerQueueRegistry;
    }

    @Override
    public int interact(Connection connection) {
        if (this.session == null) throw new IllegalStateException("The session is null");

        try {
            GameTicket ticket = playerQueueRegistry.registerUserAwaiting(session.getUsername());
            connection.sendMessage(
                    "You've been put into the game finding queue. You can enter \"!exit\" to exit.");

            if (nextInteractor instanceof SessionDialogInteractor sessionDialogInteractor) {
                sessionDialogInteractor.setSession(session);
            }

            String input;
            while (true) {
                if (connection.readyToRead()) {
                    input = connection.receiveMessage();
                    super.checkForExit(input);
                }
                if (playerQueueRegistry.checkPlayerTurn(ticket)) {
                    connection.sendMessage("Your turn's come!");
                    return this.nextInteractor.interact(connection);
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Something went wrong", e);
        } catch (TerminationException e) {
            logger.log(Level.CONFIG, "A user went away before game started");
            session.close();
            return 0; // OK
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "The user session wasn't found in the registry. This shouldn't have happened");
        } finally {
            session.setGameTicket(null);
        }
        return -1; // ERROR
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
