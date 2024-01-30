package org.rsp.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class CompoundRegistryTest {

    private final CompoundRegistry compoundRegistry = new CompoundRegistry();

    @Test
    void testSessionCreationPurging() {
        String user = "Sarah";
        Session session = compoundRegistry.createSession(user);
        Assertions.assertEquals(user, compoundRegistry.findSession(user).getUsername());

        session.close();
        Assertions.assertFalse(session.isActive());
    }

    @Test
    void testPlayerQueuingIfNoPlayer() {
        Assertions.assertNull(compoundRegistry.registerUserAwaiting("Lara"));
    }

    @Test
    void testPlayerQueuingPlayerExistsWithCanceling() {
        Session session = compoundRegistry.createSession("Mary");
        QueueTicket ticket = compoundRegistry.registerUserAwaiting(session.getUsername());

        Assertions.assertTrue(compoundRegistry.isUserAwaiting(session.getUsername()));
        compoundRegistry.cancelQueueParticipation(session.getUsername());
        Assertions.assertFalse(compoundRegistry.isUserAwaiting(session.getUsername()));
        assertFalse(ticket.isActive());
        assertNull(session.getQueueTicket());
        assertTrue(session.isActive());
    }

    @Test
    void testPlayerQueuingPlayerExistsWithSessionClosure() {
        Session session = compoundRegistry.createSession("Sophie");
        QueueTicket ticket = compoundRegistry.registerUserAwaiting(session.getUsername());

        Assertions.assertTrue(compoundRegistry.isUserAwaiting(session.getUsername()));
        session.close();
        Assertions.assertFalse(compoundRegistry.isUserAwaiting(session.getUsername()));
        assertFalse(ticket.isActive());
        assertNull(session.getQueueTicket());
        assertFalse(session.isActive());
    }
}