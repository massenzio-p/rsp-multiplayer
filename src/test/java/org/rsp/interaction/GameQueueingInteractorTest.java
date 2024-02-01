package org.rsp.interaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rsp.network.Connection;
import org.rsp.network.PlayerConnection;
import org.rsp.network.session.PlayerSession;
import org.rsp.network.session.Session;
import org.rsp.registry.queue.PlayerQueueRegistry;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class GameQueueingInteractorTest {

    @Mock
    private TestSessionInteractor nextInteractor;
    @Mock
    private PlayerQueueRegistry playerQueueRegistry;
    @Mock
    private Connection connection;
    private GameQueueingInteractor interactor;
    private Session testSession;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        this.interactor = new GameQueueingInteractor(this.nextInteractor, this.playerQueueRegistry);
        this.testSession = new PlayerSession("Laura", null);

        interactor.setSession(testSession);

        Mockito.when(connection.readyToRead()).thenReturn(true);
    }

    @Test
    void testInteractorIfSessionIsNull() {
        interactor.setSession(null);
        assertThrows(
                IllegalStateException.class,
                () -> interactor.interact(Mockito.mock(Connection.class))
        );
    }

    @Test
    void testInteractorIfExit() throws IOException {
        Mockito.when(this.connection.receiveMessage()).thenReturn("!exit");
        Session sessionToClose = Mockito.mock(Session.class);
        interactor.setSession(sessionToClose);
        interactor.interact(connection);
        Mockito.verify(nextInteractor, Mockito.never()).interact(any());
        Mockito.verify(sessionToClose, Mockito.times(1)).close();
    }

    @Test
    void testInteractor() throws IOException {
        connection = Mockito.mock(PlayerConnection.class);
        Mockito.when(connection.readyToRead()).thenReturn(false);
        Mockito.when(playerQueueRegistry.checkPlayerTurn(Mockito.any())).thenReturn(true);
        interactor.interact(connection);

        String msg = "You've been put into the game finding queue. You can enter \"!exit\" to exit.";
        Mockito.verify(connection, Mockito.times(1)).sendMessage(ArgumentMatchers.eq(msg));
        Mockito.verify(connection, Mockito.never()).receiveMessage();
        Mockito.verify(nextInteractor, Mockito.times(1)).interact(any());
    }
}