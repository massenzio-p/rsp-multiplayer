package org.rsp.interaction.game;

import org.rsp.network.Connection;
import org.rsp.registry.queue.GameTicket;
import org.rsp.util.Closeable;

public interface GameRoom extends AutoCloseable {
    void addPlayer(GameTicket gameTicket);
    boolean isReady();
    int getPlayersAmount();
    void connect(GameTicket gameTicket, Connection connection);
}