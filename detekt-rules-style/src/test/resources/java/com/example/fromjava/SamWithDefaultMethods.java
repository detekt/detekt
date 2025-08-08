package com.example.fromjava;

@FunctionalInterface
public interface SamWithDefaultMethods {
    void foo();
    default void bar() {}
    default void baz() {}
}
