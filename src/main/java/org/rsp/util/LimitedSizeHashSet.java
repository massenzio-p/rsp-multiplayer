package org.rsp.util;

import java.util.Collection;
import java.util.HashSet;

public class LimitedSizeHashSet<T> extends HashSet<T> {

    private final int maxSize;

    public LimitedSizeHashSet(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() == maxSize) throw new IllegalStateException("Maximum size reached");
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c.size() + size() > maxSize) {
            throw new IllegalStateException("There is not enough room for this collection");
        }
        return super.addAll(c);
    }
}
