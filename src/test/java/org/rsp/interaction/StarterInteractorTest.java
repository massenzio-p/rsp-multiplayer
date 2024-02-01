package org.rsp.interaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rsp.network.Connection;
import org.rsp.network.PlayerConnection;
import org.rsp.network.session.PlayerSession;
import org.rsp.network.session.SessionRegistry;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class StarterInteractorTest {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    void testInteraction() throws IOException, ExecutionException, InterruptedException {
        TestSessionInteractor nextInteractor = Mockito.mock(TestSessionInteractor.class);
        SessionRegistry sessionRegistry = Mockito.mock(SessionRegistry.class);
        StarterInteractor interactor =
                new StarterInteractor(nextInteractor, sessionRegistry);

        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        Connection connection = new PlayerConnection(bufferedReader, printWriter);

        Mockito.when(bufferedReader.readLine())
                .thenReturn("Kate")
                .thenReturn(null)
                .thenReturn("")
                .thenReturn("Judy");

        Mockito.when(sessionRegistry.findSession(Mockito.any()))
                .thenReturn(new PlayerSession("Kate", null))
                .thenReturn(null); // Judy

        Future<?> future = executorService.submit(() -> interactor.interact(connection));
        future.get();

        String resp = outputStream.toString();
        Assertions.assertEquals(
                "RPS Game Server is greeting you! What's your name?\n" +
                        "A user with such a name is already playing RSP, choose different one, please. " +
                        "Or type \"!exit\" to exit.\n" +
                        "Please, enter a valid non-empty name. Or type \"!exit\" to exit.\n" +
                        "Please, enter a valid non-empty name. Or type \"!exit\" to exit.\n" +
                        "Nice to meet you, Judy!\n",
                resp);
        Mockito.verify(nextInteractor, Mockito.times(1)).setSession(Mockito.any());
        Mockito.verify(nextInteractor, Mockito.times(1)).interact(Mockito.any());
    }

    // TODO: Check exit
}