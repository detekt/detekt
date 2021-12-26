package com.example.fromjava;

@FunctionalInterface
interface SamWithDefaultMethods {
    void foo();
    default void bar() {}
    default void baz() {}
}
