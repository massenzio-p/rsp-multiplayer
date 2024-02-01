package org.rsp.network;

import java.io.Closeable;
import java.io.IOException;

public interface Connection extends Closeable {
    String receiveMessage() throws IOException;
    void sendMessage(String msg);

    boolean readyToRead() throws IOException;
}
