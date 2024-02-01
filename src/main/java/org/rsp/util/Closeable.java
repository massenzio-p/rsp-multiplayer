package org.rsp.util;

public interface Closeable {
    boolean isActive();
    void close();
}
