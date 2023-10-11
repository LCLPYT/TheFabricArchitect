package com.simibubi.mightyarchitect.util;

import net.minecraft.client.renderer.RenderType;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class PredicateIterator<T> implements Iterator<T> {

    private final Iterator<T> parent;
    private final Predicate<T> predicate;

    public PredicateIterator(Iterator<T> parent, Predicate<T> predicate) {
        this.parent = parent;
        this.predicate = predicate;
    }

    T next = null;

    @Override
    public boolean hasNext() {
        if (next == null) {
            advance();
        }

        return next != null;
    }

    private void advance() {
        while (parent.hasNext()) {
            T candidate = parent.next();

            if (!predicate.test(candidate)) continue;

            next = candidate;
            break;
        }
    }

    @Override
    public T next() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        T ret = next;
        next = null;

        return ret;
    }
}
