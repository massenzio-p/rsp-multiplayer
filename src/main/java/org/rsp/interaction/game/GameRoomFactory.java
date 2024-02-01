package org.rsp.interaction.game;

import org.rsp.interaction.game.GameRoom;

public interface GameRoomFactory {
    GameRoom createGameRoom(int playersAmount);
}
