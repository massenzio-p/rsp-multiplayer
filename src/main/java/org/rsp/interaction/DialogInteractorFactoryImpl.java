package org.rsp.interaction;

import lombok.RequiredArgsConstructor;
import org.rsp.registry.PlayerQueueRegistry;
import org.rsp.registry.SessionRegistry;

@RequiredArgsConstructor
public class DialogInteractorFactoryImpl implements DialogInteractorFactory {

    private final SessionRegistry sessionRegistry;
    private final PlayerQueueRegistry playerQueueRegistry;

    @Override
    public DialogInteractor createInteractor(Stage stage) {
        return switch (stage) {
            case INTRODUCTION -> new IntroductionInteractor(
                    createInteractor(Stage.MENU),
                    this.sessionRegistry,
                    this.playerQueueRegistry);
            default -> throw new IllegalArgumentException("Unexpected stage");
        };
    }
}
