package org.rsp.network;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.rsp.interaction.DialogInteractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ClientSocketHandlerTest {

    @Mock
    private Socket socket;
    @Mock
    private InputStream mockInputStream;
    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private DialogInteractor dialogInteractor;
    private Runnable socketHandler;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.socketHandler = new ClientSocketHandler(socket, dialogInteractor);
        Mockito.when(socket.getInputStream()).thenReturn(mockInputStream);
        Mockito.when(socket.getOutputStream()).thenReturn(mockOutputStream);
    }

    @Test
    void testNormalHandleSocket() throws InterruptedException {
        executorService.submit(socketHandler);
        Thread.sleep(2000);
        Mockito.verify(dialogInteractor, Mockito.only()).interact(Mockito.any(), Mockito.any());
    }
}