package org.rsp.interaction;

import org.rsp.exception.TerminationException;
import org.rsp.network.Connection;
import org.rsp.network.session.Session;
import org.rsp.network.session.SessionRegistry;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class StarterInteractor extends AbstractDialogInteractor implements DialogInteractor {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final SessionRegistry sessionRegistry;
    private final DialogInteractor nextInteractor;


    StarterInteractor(DialogInteractor nextInteractor, SessionRegistry sessionRegistry) {
        this.nextInteractor = nextInteractor;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public int interact(Connection connection) {
        try {
            connection.sendMessage("RPS Game Server is greeting you! What's your name?");
            String name;
            // unless the user types valid non-registered name or exits
            do {
                name = connection.receiveMessage();
                if (isInvalidName(name)) {
                    name = null;
                    connection.sendMessage("Please, enter a valid non-empty name. Or type \"!exit\" to exit.");
                    continue;
                }
                super.checkForExit(name);
                if (sessionRegistry.findSession(name) != null) {
                    name = null;
                    connection.sendMessage(
                            "A user with such a name is already playing RSP, choose different one, please. " +
                                    "Or type \"!exit\" to exit.");
                }
            } while (name == null);

            Session session = this.sessionRegistry.createSession(name);

            int exitCode = 1;
            while (exitCode == 1) {
                if (nextInteractor instanceof SessionDialogInteractor sessionDialogInteractor) {
                    sessionDialogInteractor.setSession(session);
                }
                connection.sendMessage(String.format("Nice to meet you, %s!", name));
                exitCode = this.nextInteractor.interact(connection);
            }

            connection.close();
            return 0;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Something went wrong", e);
        } catch (TerminationException e) {
            logger.log(Level.CONFIG, "A user went away before successful login");
            return 0;
        }
        return -1;
    }

    private boolean isInvalidName(String name) {
        return name == null || name.isEmpty();
    }
}
