package org.rsp.registry;

import lombok.RequiredArgsConstructor;
import org.rsp.network.session.PlayerSession;
import org.rsp.network.session.Session;
import org.rsp.network.session.SessionRegistry;
import org.rsp.registry.game.GameRegistry;
import org.rsp.interaction.game.GameRoom;
import org.rsp.interaction.game.GameRoomFactory;
import org.rsp.registry.queue.GameTicket;
import org.rsp.registry.queue.PlayerGameTicket;
import org.rsp.registry.queue.PlayerQueueRegistry;
import org.rsp.util.LimitedSizeHashSet;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CompoundRegistry implements PlayerQueueRegistry, GameRegistry, SessionRegistry {

    private static final int QUEUE_CAPACITY = 15;
    private static final int PLAYER_NUMBER_PER_ROOM_SUPPORT = 2;
    public static final int MAX_GAMES_NUMBER = 20;

    private final ConcurrentHashMap<String, Session> sessionStorage = new ConcurrentHashMap<>();
    private final Queue<GameTicket> playerQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private final GameRoom[] currentlyFillingRooms = new GameRoom[PLAYER_NUMBER_PER_ROOM_SUPPORT];
    private final Set<GameRoom> gamesStorage = Collections.synchronizedSet(new LimitedSizeHashSet<>(MAX_GAMES_NUMBER));
    private final GameRoomFactory gameRoomFactory;

    @Override
    public Session createSession(String username) {
        Session session = new PlayerSession(username, null);

        this.sessionStorage.put(username, session);

        return session;
    }

    @Override
    public Session findSession(String username) {
        return this.sessionStorage.get(username);
    }

    @Override
    public void purgeSession(Session session) {
        if (session != null) {
            cancelQueueParticipation(session.getUsername());
            this.sessionStorage.remove(session.getUsername());
        }
    }

    @Override
    public void cancelQueueParticipation(String username) {
        Session session = findSession(username);

        if (session != null && session.getGameTicket() != null) {
            session.getGameTicket().close();
            session.setGameTicket(null);
        }
        synchronized (this.playerQueue) {
            this.playerQueue.remove(username);
        }
    }

    @Override
    public boolean isUserAwaiting(String name) {
        Session session = findSession(name);

        return session != null
                && session.getGameTicket() != null
                && session.getGameTicket().isActive();
    }

    @Override
    public GameTicket registerUserAwaiting(String name) throws NoSuchElementException, IllegalStateException {
        Session session = findSession(name);
        if (session != null) {
            GameTicket gameTicket = new PlayerGameTicket(name);
            session.setGameTicket(gameTicket);
            synchronized (this.playerQueue) {
                this.playerQueue.add(gameTicket);
            }
            return gameTicket;
        }
        throw new NoSuchElementException("The user not found in registry");
    }

    @Override
    public GameRoom findGameRoom(GameTicket gameTicket) {
        int playersAmount = gameTicket.getRoomSize();
        synchronized (this.currentlyFillingRooms) {
            GameRoom room = this.currentlyFillingRooms[playersAmount - 1];
            if (room == null) {
                room = this.gameRoomFactory.createGameRoom(playersAmount);
                this.currentlyFillingRooms[playersAmount - 1] = room;
            }
            room.addPlayer(gameTicket);

            if (room.getPlayersAmount() == playersAmount) {
                this.gamesStorage.add(room);
                this.currentlyFillingRooms[playersAmount - 1] = null;
            }
            return room;
        }
    }

    @Override
    public boolean checkPlayerTurn(GameTicket ticket) {
        GameTicket nextRequest = this.playerQueue.peek();

        if (nextRequest == null || ticket == null || !ticket.isActive()) {
            throw new IllegalCallerException("The request is invalid or unregistered");
        }
        if (ticket.equals(nextRequest)) {
            synchronized (this.playerQueue) {
                nextRequest = this.playerQueue.poll();
            }
            GameRoom room = findGameRoom(nextRequest);
            ticket.setGameRoom(room);
            return true;
        }
        return false;
    }

    @Override
    public void removeRoom(GameRoom gameRoom) {
        gamesStorage.remove(gameRoom);
    }
}
