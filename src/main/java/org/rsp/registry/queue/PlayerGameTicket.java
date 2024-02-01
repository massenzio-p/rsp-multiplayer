package org.rsp.registry.queue;

import lombok.*;
import org.rsp.interaction.game.GameRoom;

import java.time.LocalDateTime;

@EqualsAndHashCode
@Data
@RequiredArgsConstructor
public class PlayerGameTicket implements GameTicket {

    private final String username;
    @Setter(AccessLevel.PRIVATE)
    private boolean active = true;
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime startAwaitingTime = LocalDateTime.now();
    private GameRoom gameRoom;
    private final int roomSize = 2;

    @Override
    public void close() {
        this.active = false;
    }
}
