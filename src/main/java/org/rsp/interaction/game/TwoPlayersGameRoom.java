package org.rsp.interaction.game;

import org.rsp.interaction.AbstractDialogInteractor;
import org.rsp.network.Connection;
import org.rsp.registry.queue.GameTicket;
import org.rsp.state.ContextHolder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class TwoPlayersGameRoom extends AbstractDialogInteractor implements GameRoom {

    private final UUID uuid = UUID.randomUUID();

    private Map<String, PlayerState> players = new ConcurrentHashMap<>();
    private Map<String, GameTicket> tickets = new ConcurrentHashMap<>();
    private CompletableFuture<GameTicket> winnerDeterminerFuture;

    private GameTicket getWinnerDeterminer() {
        Supplier<Stream<PlayerState>> responseStateStreamSupplier = () -> this.players.values()
                .stream()
                .filter(s -> s.toString().contains("RESPONSE"));

        Iterator<String> playersIterator = players.keySet().iterator();
        GameTicket firstPlayer = tickets.get(playersIterator.next());
        GameTicket secondPlayer = tickets.get(playersIterator.next());

        while (responseStateStreamSupplier.get().count() < 2) {
            if (!firstPlayer.isActive()) {
                players.put(firstPlayer.getUsername(), PlayerState.DISCONNECTED);
                return secondPlayer;
            }
            if (!secondPlayer.isActive()) {
                players.put(secondPlayer.getUsername(), PlayerState.DISCONNECTED);
                return firstPlayer;
            }
        }
        GameMove firstPlayerMove = GameMove.valueOf(players.get(firstPlayer.getUsername())
                .toString()
                .replace("_RESPONSE", ""));
        GameMove secondPlayerMove = GameMove.valueOf(players.get(secondPlayer.getUsername())
                .toString()
                .replace("_RESPONSE", ""));

        if (firstPlayerMove == secondPlayerMove) {
            // draw
            return null;
        } else if (firstPlayerMove.getNext() == secondPlayerMove) {
            // second winner
            return secondPlayer;
        }
        // first winner
        return firstPlayer;
    }

    private enum PlayerState {
        ENTERED, CONNECTED, ROCK_RESPONSE, SCISSORS_RESPONSE, PAPER_RESPONSE, DISCONNECTED
    }

    @Override
    public void addPlayer(GameTicket gameTicket) {
        players.put(gameTicket.getUsername(), PlayerState.ENTERED);
        tickets.put(gameTicket.getUsername(), gameTicket);
    }

    @Override
    public boolean isReady() {
        return this.players.size() == 2;
    }

    @Override
    public int getPlayersAmount() {
        return this.players.size();
    }

    @Override
    public void connect(GameTicket gameTicket, Connection connection) {
        if (gameTicket == null) throw new IllegalArgumentException("Game request is null");

        this.players.put(gameTicket.getUsername(), PlayerState.CONNECTED);
        Supplier<Stream<PlayerState>> connectedStatesStreamSupplier = () -> players.values()
                .stream()
                .filter(s -> s == PlayerState.CONNECTED);

        while (connectedStatesStreamSupplier.get().count() < getPlayersAmount()) {
            // TODO: Add timeout.
        }

        String rivalName = players.keySet()
                .stream()
                .filter(key -> !key.equals(gameTicket.getUsername()))
                .findFirst()
                .get();

        connection.sendMessage(rivalName + " connected.");
        connection.sendMessage("The game started. Enter your move! (Rock|R|/Scissors|S/Paper|P)");
        playTheGame(gameTicket, connection);
        gameTicket.close();
    }

    private void playTheGame(GameTicket gameTicket, Connection connection) {
        while (true) {
            try {
                handleResponse(gameTicket, connection);
                if (this.winnerDeterminerFuture == null) {
                    this.winnerDeterminerFuture = CompletableFuture.supplyAsync(this::getWinnerDeterminer);
                }
                GameTicket winner = winnerDeterminerFuture.join();
                if (winner == null) {
                    connection.sendMessage("That's a draw! Next round!");
                    reinit();
                } else if (winner.getUsername().equals(gameTicket.getUsername())) {
                    connection.sendMessage("You're a winner, congrats!");
                    break;
                } else {
                    connection.sendMessage("Sorry, but you're a looser this time!");
                    break;
                }
            } catch (IOException e) {
                this.players.put(gameTicket.getUsername(), PlayerState.DISCONNECTED);
                gameTicket.close();
            } catch (Exception e) {
                System.out.println();
            }
        }
    }

    private synchronized void reinit() {
        if (this.winnerDeterminerFuture.isDone()) {
            this.winnerDeterminerFuture = CompletableFuture.supplyAsync(this::getWinnerDeterminer);
            for (var key : this.players.keySet()) {
                players.put(key, PlayerState.CONNECTED);
            }
        }
    }

    private void handleResponse(GameTicket request, Connection connection) throws IOException {
        Set<String> acceptableResponses = Set.of("rock", "r", "scissors", "s", "paper", "p");
        String input;
        while (true) {
            input = connection.receiveMessage();
            if (input == null) {
                throw new IOException();
            }
            if (!input.isEmpty() && acceptableResponses.contains(input.toLowerCase())) {
                players.put(request.getUsername(), mapMove(input));
                break;
            }
            connection.sendMessage("Can't recognise your input. Please, try again (Rock|R|/Scissors|S/Paper|P)");
        }
    }

    private PlayerState mapMove(String input) {
        return switch (input.toLowerCase()) {
            case "rock", "r" -> PlayerState.ROCK_RESPONSE;
            case "scissors", "s" -> PlayerState.SCISSORS_RESPONSE;
            case "paper", "p" -> PlayerState.PAPER_RESPONSE;
            default -> throw new IllegalArgumentException("Illegal move");
        };
    }

    @Override
    public void close() {
        ContextHolder.getGameRegistry().removeRoom(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TwoPlayersGameRoom that = (TwoPlayersGameRoom) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
