package org.rsp.interaction;

import org.rsp.exception.TerminationException;
import org.rsp.registry.PlayerQueueRegistry;
import org.rsp.registry.Session;
import org.rsp.registry.SessionRegistry;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class IntroductionInteractor extends AbstractDialogInteractor implements DialogInteractor {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final SessionRegistry sessionRegistry;
    private final PlayerQueueRegistry playerQueueRegistry;
    private final DialogInteractor nextInteractor;


    IntroductionInteractor(
            DialogInteractor nextInteractor,
            SessionRegistry sessionRegistry,
            PlayerQueueRegistry playerQueueRegistry) {
        this.nextInteractor = nextInteractor;
        this.sessionRegistry = sessionRegistry;
        this.playerQueueRegistry = playerQueueRegistry;
    }

    @Override
    public void interact(Reader reader, Writer writer) {
        PrintWriter printWriter = super.wrapWriter(writer);
        BufferedReader bufferedReader = super.wrapReader(reader);

        try {
            printWriter.println("RPS Game Server is greeting you! What's your name?");
            String name;
            // unless the user types valid non-registered name or exits
            do {
                name = bufferedReader.readLine();
                if (isInvalidName(name)) {
                    name = null;
                    printWriter.println("Please, enter a valid non-empty name. Or type \"!exit\" to exit.");
                    continue;
                }
                super.checkForExit(name);
                if (playerQueueRegistry.isUserAwaiting(name)) {
                    name = null;
                    printWriter.println(
                            "A user with such a name is already playing RSP, choose different one, please. " +
                                    "Or type \"!exit\" to exit.");
                } else {
                    playerQueueRegistry.registerUserAwaiting(name);
                    logger.info(String.format("The user [%s] registered.", name));
                    printWriter.printf("Nice to meet your, %s!%n", name);
                }
            } while (name == null);
            Session session = this.sessionRegistry.createSession(name);
            if (nextInteractor instanceof SessionDialogInteractor sessionDialogInteractor) {
                sessionDialogInteractor.setSession(session);
            }
            this.nextInteractor.interact(reader, writer);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Something went wrong", e);
        } catch (TerminationException e) {
            logger.log(Level.CONFIG, "A user went away before successful login");
        }
    }

    private boolean isInvalidName(String name) {
        return name == null || name.isEmpty();
    }
}
