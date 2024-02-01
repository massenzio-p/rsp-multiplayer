package org.rsp.interaction.game;

public enum GameMove {
    ROCK, PAPER, SCISSORS;

    GameMove getNext() {
        if (this == SCISSORS) {
            return ROCK;
        } else {
            return values()[ordinal() + 1];
        }
    }

    GameMove getPrevious() {
        if (this == ROCK) {
            return SCISSORS;
        }
        return values()[ordinal() - 1];
    }
}
