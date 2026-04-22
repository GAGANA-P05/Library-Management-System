package com.library.factory;

public interface EntityFactory<T> {
    T create(Object... params);
}
