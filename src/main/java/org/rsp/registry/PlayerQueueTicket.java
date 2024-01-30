package org.rsp.registry;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Closeable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class PlayerQueueTicket implements QueueTicket {

    private final String username;
    @Setter(AccessLevel.PRIVATE)
    private boolean active = true;
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime startAwaitingTime = LocalDateTime.now();

    @Override
    public void close() {
        this.active = false;
    }
}
