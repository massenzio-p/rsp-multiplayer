package org.rsp.network.session;

import org.rsp.network.session.Session;

public interface SessionRegistry {

    Session createSession(String username);
    void purgeSession(Session session);
    Session findSession(String username);
}
