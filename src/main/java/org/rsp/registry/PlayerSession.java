package org.rsp.registry;

import lombok.*;
import org.rsp.ContextHolder;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Data
final class PlayerSession implements Session {
    
    private final String username;
    private QueueTicket queueTicket;
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastActivityTime = LocalDateTime.now();
    @Setter(AccessLevel.PRIVATE)
    private boolean active = true;

    public PlayerSession(String username, QueueTicket queueTicket) {
        this.username = username;
        this.queueTicket = queueTicket;
    }

    @Override
    public void refreshActivity() {
        this.lastActivityTime = LocalDateTime.now();
    }

    @Override
    public void close() {
        ContextHolder.getSessionRegistry().purgeSession(this);
        this.active = false;
        if (this.queueTicket != null) {
            this.queueTicket.close();
            this.queueTicket = null;
        }
    }
}
