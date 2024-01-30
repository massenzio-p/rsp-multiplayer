package org.rsp.network;

import java.net.Socket;

public interface SocketHandlerFactory {

    Runnable createSocketHandler(Socket socket);
}
