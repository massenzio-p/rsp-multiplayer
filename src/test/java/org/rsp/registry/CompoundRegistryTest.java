package org.rsp.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rsp.interaction.game.GameRoom;
import org.rsp.interaction.game.TwoPlayersGameRoom;
import org.rsp.network.session.Session;
import org.rsp.interaction.game.GameRoomFactory;
import org.rsp.registry.queue.GameTicket;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class CompoundRegistryTest {

    @Mock
    private GameRoomFactory gameRoomFactory;
    private CompoundRegistry compoundRegistry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        compoundRegistry = new CompoundRegistry(gameRoomFactory);
    }

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
        Assertions.assertThrows(
                NoSuchElementException.class,
                () -> compoundRegistry.registerUserAwaiting("Lara")
        );
    }

    @Test
    void testPlayerQueuingPlayerExistsWithCanceling() {
        Session session = compoundRegistry.createSession("Mary");
        GameTicket ticket = compoundRegistry.registerUserAwaiting(session.getUsername());

        Assertions.assertTrue(compoundRegistry.isUserAwaiting(session.getUsername()));
        compoundRegistry.cancelQueueParticipation(session.getUsername());
        Assertions.assertFalse(compoundRegistry.isUserAwaiting(session.getUsername()));
        assertFalse(ticket.isActive());
        assertNull(session.getGameTicket());
        assertTrue(session.isActive());
    }

    @Test
    void testPlayerQueuingPlayerExistsWithSessionClosure() {
        Session session = compoundRegistry.createSession("Sophie");
        GameTicket ticket = compoundRegistry.registerUserAwaiting(session.getUsername());

        Assertions.assertTrue(compoundRegistry.isUserAwaiting(session.getUsername()));
        session.close();
        Assertions.assertFalse(compoundRegistry.isUserAwaiting(session.getUsername()));
        assertFalse(ticket.isActive());
        assertNull(session.getGameTicket());
        assertFalse(session.isActive());
    }

    @Test
    void testFindGameRoom() {
        Mockito.when(gameRoomFactory.createGameRoom(2))
                .thenAnswer(inv -> new TwoPlayersGameRoom());
        Session session = compoundRegistry.createSession("Diana");
        GameTicket ticket = compoundRegistry.registerUserAwaiting(session.getUsername());
        GameRoom gameRoom = compoundRegistry.findGameRoom(ticket);

        Session session2 = compoundRegistry.createSession("Christina");
        GameTicket ticket2 = compoundRegistry.registerUserAwaiting(session2.getUsername());
        GameRoom gameRoom2 = compoundRegistry.findGameRoom(ticket2);

        Session session3 = compoundRegistry.createSession("Margo");
        GameTicket ticket3 = compoundRegistry.registerUserAwaiting(session3.getUsername());
        GameRoom gameRoom3 = compoundRegistry.findGameRoom(ticket3);

        assertTrue(compoundRegistry.isUserAwaiting(session.getUsername()));
        assertTrue(compoundRegistry.isUserAwaiting(session2.getUsername()));
        assertTrue(compoundRegistry.isUserAwaiting(session3.getUsername()));
        assertFalse(compoundRegistry.checkPlayerTurn(ticket3));
        assertTrue(compoundRegistry.checkPlayerTurn(ticket));
        assertFalse(compoundRegistry.checkPlayerTurn(ticket3));
        assertTrue(compoundRegistry.checkPlayerTurn(ticket2));
        assertTrue(compoundRegistry.checkPlayerTurn(ticket3));

        Assertions.assertSame(gameRoom, gameRoom2);
        Assertions.assertNotSame(gameRoom3, gameRoom);
    }
}