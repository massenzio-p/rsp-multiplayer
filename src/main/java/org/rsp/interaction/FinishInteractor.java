package org.rsp.interaction;

import jdk.jshell.spi.ExecutionControl;
import org.rsp.network.Connection;
import org.rsp.network.session.Session;

import java.io.Reader;
import java.io.Writer;

public class FinishInteractor implements DialogInteractor, SessionDialogInteractor {

    private Session session;

    @Override
    public int interact(Connection connection) {
        connection.sendMessage("See you =)");
        session.close();
        return 0;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }
}
