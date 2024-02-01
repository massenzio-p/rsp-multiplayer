package org.rsp.network.session;

import org.rsp.util.Closeable;
import org.rsp.registry.queue.GameTicket;

import java.time.LocalDateTime;

public interface Session extends Closeable {

    String getUsername();
    GameTicket getGameTicket();
    void setGameTicket(GameTicket ticket);
    LocalDateTime getLastActivityTime();
    void refreshActivity();
}
