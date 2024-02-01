package org.rsp.registry.queue;

import org.rsp.util.Closeable;
import org.rsp.interaction.game.GameRoom;

import java.time.LocalDateTime;

public interface GameTicket extends Closeable {

    String getUsername();
    LocalDateTime getStartAwaitingTime();
    int getRoomSize();
    void setGameRoom(GameRoom room);
    GameRoom getGameRoom();
}
