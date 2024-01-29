package org.rsp.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rsp.interaction.DialogInteractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.Executors;

class NonBlockingSocketHandlerTest {

    @Mock
    private Socket socket;
    @Mock
    private InputStream mockInputStream;
    @Mock
    private DialogInteractor dialogInteractor;
    private NonBlockingSocketHandler socketHandler;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.socketHandler = new NonBlockingSocketHandler(Executors.newSingleThreadExecutor(), dialogInteractor);
        Mockito.when(socket.getInputStream()).thenReturn(mockInputStream);
        Mockito.doReturn(System.out).when(socket).getOutputStream();
    }

    @Test
    void testNormalHandleSocket() throws InterruptedException {
        socketHandler.handleSocket(socket);
        Thread.sleep(2000);
        Mockito.verify(dialogInteractor, Mockito.only()).interact(Mockito.any(), Mockito.any());
    }
}