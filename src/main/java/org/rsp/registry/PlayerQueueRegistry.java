package org.rsp.registry;

public interface PlayerQueueRegistry {
    void cancelQueueParticipation(String username);

    boolean isUserAwaiting(String name);

    QueueTicket registerUserAwaiting(String name);
}
