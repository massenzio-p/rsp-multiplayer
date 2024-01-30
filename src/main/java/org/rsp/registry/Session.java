package org.rsp.registry;

import java.io.Closeable;
import java.time.LocalDateTime;

public interface Session extends Closable {

    String getUsername();
    QueueTicket getQueueTicket();
    void setQueueTicket(QueueTicket ticket);
    LocalDateTime getLastActivityTime();
    void refreshActivity();
}
