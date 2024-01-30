package org.rsp.interaction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.rsp.registry.PlayerQueueRegistry;
import org.rsp.registry.SessionRegistry;

import java.io.*;
import java.util.concurrent.*;

class IntroductionInteractorTest {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Test
    void testInteraction() throws IOException, ExecutionException, InterruptedException {
        DialogInteractor nextInteractor = Mockito.mock(DialogInteractor.class);
        PlayerQueueRegistry playerQueueRegistry = Mockito.mock(PlayerQueueRegistry.class);
        SessionRegistry sessionRegistry = Mockito.mock(SessionRegistry.class);
        IntroductionInteractor interactor =
                new IntroductionInteractor(nextInteractor, sessionRegistry, playerQueueRegistry);

        BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream, true);

        Mockito.when(bufferedReader.readLine())
                .thenReturn("Kate")
                .thenReturn(null)
                .thenReturn("")
                .thenReturn("Judy");

        Mockito.when(playerQueueRegistry.isUserAwaiting(Mockito.any()))
                .thenReturn(true) // Kate
                .thenReturn(false); // Judy
        Future<?> future = executorService.submit(() -> interactor.interact(bufferedReader, printWriter));
        future.get();

        String resp = outputStream.toString();
        Assertions.assertEquals(
                "RPS Game Server is greeting you! What's your name?\n" +
                        "A user with such a name is already playing RSP, choose different one, please. " +
                        "Or type \"!exit\" to exit.\n" +
                        "Please, enter a valid non-empty name. Or type \"!exit\" to exit.\n" +
                        "Please, enter a valid non-empty name. Or type \"!exit\" to exit.\n" +
                        "Nice to meet your, Judy!\n",
                resp);
        Mockito.verify(playerQueueRegistry, Mockito.times(1)).registerUserAwaiting(Mockito.any());
        Mockito.verify(nextInteractor, Mockito.times(1)).interact(Mockito.any(), Mockito.any());
    }
}