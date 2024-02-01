package org.rsp.interaction;

import org.rsp.network.Connection;

import java.io.Reader;
import java.io.Writer;

public interface DialogInteractor {
    int interact(Connection connection);
}
