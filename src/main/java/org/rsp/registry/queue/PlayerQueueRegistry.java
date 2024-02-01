package org.rsp.registry.queue;

import java.util.NoSuchElementException;

public interface PlayerQueueRegistry {
    void cancelQueueParticipation(String username);

    boolean isUserAwaiting(String name);

    GameTicket registerUserAwaiting(String name) throws NoSuchElementException;

    boolean checkPlayerTurn(GameTicket ticket);
}
