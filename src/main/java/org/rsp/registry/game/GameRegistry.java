package org.rsp.registry.game;

import org.rsp.interaction.game.GameRoom;
import org.rsp.interaction.game.TwoPlayersGameRoom;
import org.rsp.registry.queue.GameTicket;

public interface GameRegistry {
    GameRoom findGameRoom(GameTicket gameTicket);

    void removeRoom(GameRoom twoPlayersGameRoom);
}
