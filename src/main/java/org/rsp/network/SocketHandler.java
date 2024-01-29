package org.rsp.network;

import java.net.Socket;

/**
 * An interface for socket handler implementations.
 */
public interface SocketHandler {

    /**
     * The method is supposed to handle established sockets.
     *
     * @param socket - socket to handle.
     */
    void handleSocket(Socket socket);
}
