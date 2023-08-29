package com.sloth.rx;

@FunctionalInterface
public interface Creator<T> {
    T create() throws Exception;
}
