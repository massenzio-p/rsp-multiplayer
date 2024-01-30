package org.rsp.registry;

import java.time.LocalDateTime;

public interface QueueTicket extends Closable {

    String getUsername();
    LocalDateTime getStartAwaitingTime();
}
