package org.rsp.registry;

public interface SessionRegistry {

    Session createSession(String username);
    void purgeSession(Session session);
    Session findSession(String username);
}
