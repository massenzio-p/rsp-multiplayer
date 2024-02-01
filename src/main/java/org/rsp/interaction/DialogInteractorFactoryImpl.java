package org.rsp.interaction;

import lombok.RequiredArgsConstructor;
import org.rsp.registry.game.GameRegistry;
import org.rsp.registry.queue.PlayerQueueRegistry;
import org.rsp.network.session.SessionRegistry;

@RequiredArgsConstructor
public class DialogInteractorFactoryImpl implements DialogInteractorFactory {

    private final SessionRegistry sessionRegistry;
    private final PlayerQueueRegistry playerQueueRegistry;
    private final GameRegistry gameRegistry;

    @Override
    public DialogInteractor createInteractor(Stage stage) {
        return switch (stage) {
            case INTRODUCTION -> new StarterInteractor(createInteractor(Stage.GAME_QUEUE), this.sessionRegistry);
            case GAME_QUEUE -> new GameQueueingInteractor(createInteractor(Stage.GAME_START), this.playerQueueRegistry);
            case GAME_START -> new GameStarterInteractor(createInteractor(Stage.FINISHING), gameRegistry);
            case FINISHING -> new FinishInteractor();
        };
    }
}
