package org.rsp.interaction.game;

public class GameRoomFactoryImpl implements GameRoomFactory {

    @Override
    public GameRoom createGameRoom(int playersAmount) {
        if (playersAmount == 2) return new TwoPlayersGameRoom();
        throw new RuntimeException("Not implemented yet");
    }
}
