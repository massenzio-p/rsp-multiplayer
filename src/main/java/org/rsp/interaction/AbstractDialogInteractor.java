package org.rsp.interaction;

import org.rsp.exception.TerminationException;
import org.rsp.network.Connection;
import org.rsp.network.PlayerConnection;

import java.io.*;

public abstract class AbstractDialogInteractor {

    protected Connection wrapIntoConnection(Writer writer, Reader reader) {
        return new PlayerConnection(reader, writer);
    }

    protected void checkForExit(String input) throws TerminationException, IOException {
        if (input == null) {
            throw new IOException();
        }
        if (input.equals("!exit")) {
            throw new TerminationException();
        }
    }
}
