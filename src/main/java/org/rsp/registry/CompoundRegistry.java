package org.rsp.registry;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class CompoundRegistry implements PlayerQueueRegistry, PlayRegistry, SessionRegistry {

    private final static int QUEUE_CAPACITY = 15;

    private final ConcurrentHashMap<String, Session> sessionStorage = new ConcurrentHashMap<>();
    private final Queue<QueueTicket> playerQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);


    @Override
    public Session createSession(String username) {
        Session session = new PlayerSession(username, null);

        this.sessionStorage.put(username, session);

        return session;
    }

    @Override
    public Session findSession(String username) {
        return this.sessionStorage.get(username);
    }

    @Override
    public void purgeSession(Session session) {
        if (session != null) {
            cancelQueueParticipation(session.getUsername());
            this.sessionStorage.remove(session.getUsername());
        }
    }

    @Override
    public void cancelQueueParticipation(String username) {
        Session session = findSession(username);

        if(session != null && session.getQueueTicket() != null) {
            session.getQueueTicket().close();
            session.setQueueTicket(null);
        }
        synchronized (this.playerQueue) {
            this.playerQueue.remove(username);
        }
    }

    @Override
    public boolean isUserAwaiting(String name) {
        Session session = findSession(name);

        return session != null
                && session.getQueueTicket() != null
                && session.getQueueTicket().isActive();
    }

    @Override
    public QueueTicket registerUserAwaiting(String name) {
        Session session = findSession(name);
        if (session != null) {
            QueueTicket queueTicket = new PlayerQueueTicket(name);
            session.setQueueTicket(queueTicket);
            synchronized (this.playerQueue) {
                this.playerQueue.add(queueTicket);
            }
            return queueTicket;
        }
        return null;
    }
}
