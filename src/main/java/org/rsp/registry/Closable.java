package org.rsp.registry;

public interface Closable {
    boolean isActive();
    void close();
}
