package org.example.util;

public interface ThrowingSupplier<T, Ex extends Exception> {
    T get() throws Ex;
}
