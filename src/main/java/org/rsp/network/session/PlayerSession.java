package org.rsp.network.session;

import lombok.*;
import org.rsp.registry.queue.GameTicket;
import org.rsp.state.ContextHolder;

import java.time.LocalDateTime;
import java.util.Objects;


@Data
public final class PlayerSession implements Session {
    
    private final String username;
    private GameTicket gameTicket;
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastActivityTime = LocalDateTime.now();
    @Setter(AccessLevel.PRIVATE)
    private boolean active = true;

    public PlayerSession(String username, GameTicket gameTicket) {
        this.username = username;
        this.gameTicket = gameTicket;
    }

    @Override
    public void refreshActivity() {
        this.lastActivityTime = LocalDateTime.now();
    }

    @Override
    public void close() {
        ContextHolder.getSessionRegistry().purgeSession(this);
        this.active = false;
        if (this.gameTicket != null) {
            this.gameTicket.close();
            this.gameTicket = null;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSession that = (PlayerSession) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
