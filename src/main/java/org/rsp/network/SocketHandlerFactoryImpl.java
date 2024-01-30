package org.rsp.network;

import org.rsp.interaction.DialogInteractorFactory;
import org.rsp.interaction.DialogInteractorFactoryImpl;
import org.rsp.interaction.Stage;

import java.net.Socket;

public class SocketHandlerFactoryImpl implements SocketHandlerFactory {

    private final DialogInteractorFactory dialogInteractorFactory;

    public SocketHandlerFactoryImpl(DialogInteractorFactoryImpl interactorFactory) {
        this.dialogInteractorFactory = interactorFactory;
    }

    @Override
    public Runnable createSocketHandler(Socket socket) {
            return new ClientSocketHandler(
                    socket,
                    this.dialogInteractorFactory.createInteractor(Stage.INTRODUCTION)
            );
    }
}
