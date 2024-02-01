package org.rsp.interaction;

import org.rsp.exception.TerminationException;
import org.rsp.interaction.game.GameRoom;
import org.rsp.network.Connection;
import org.rsp.registry.game.GameRegistry;
import org.rsp.network.session.Session;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameStarterInteractor extends AbstractDialogInteractor implements DialogInteractor, SessionDialogInteractor {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final DialogInteractor nextInteractor;
    private final GameRegistry gameRegistry;

    private Session session;

    public GameStarterInteractor(DialogInteractor interactor, GameRegistry gameRegistry) {
        this.nextInteractor = interactor;
        this.gameRegistry = gameRegistry;
    }

    @Override
    public int interact(Connection connection) {
        if (this.session == null) throw new IllegalStateException("The session is null");

        if (nextInteractor instanceof SessionDialogInteractor sessionDialogInteractor) {
            sessionDialogInteractor.setSession(session);
        }

        if (session.getGameTicket() == null || session.getGameTicket().getGameRoom() == null) {
            throw new IllegalStateException("Game request or game room is null");
        }

        connection.sendMessage("Wait your peers, please.");
        try (GameRoom room = session.getGameTicket().getGameRoom()) {
            String input;
            while (true) {
                if (connection.readyToRead()) {
                    input = connection.receiveMessage();
                    super.checkForExit(input);
                }

                if (room.isReady()) {
                    room.connect(session.getGameTicket(), connection);
                    int repeat = playAnotherGame(connection);
                    if (repeat == 0) {
                        return this.nextInteractor.interact(connection);
                    }
                    return repeat;
                }
            }

        } catch (TerminationException e) {
            logger.log(Level.CONFIG, "A user went away before game started");
            session.close();
        } catch (NoSuchElementException e) {
            logger.log(Level.SEVERE, "The user session wasn't found in the registry. This shouldn't have happened");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Something went wrong", e);
        }
        return -1;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    private int playAnotherGame(Connection connection) throws IOException {
        Set<String> acceptableResponses = Set.of("yes", "y", "no", "n");
        connection.sendMessage("Would you like to play another game? (Yes/Y/No/N)");
        String response;
        while (true) {
            response = connection.receiveMessage();
            if (acceptableResponses.contains(response.toLowerCase())) {
                if (response.toLowerCase().charAt(0) == 'y') {
                    connection.sendMessage("Great, let's play a time more!");
                    return 1; // RESTART
                }
                return 0; // OK
            } else {
                connection.sendMessage("Can't recognize your answer, try again please. (Yes/Y/No/N)");
            }
        }
    }
}
