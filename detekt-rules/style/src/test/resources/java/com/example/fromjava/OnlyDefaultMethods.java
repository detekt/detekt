package com.example.fromjava;

public interface OnlyDefaultMethods {
    default void foo() {}
    default void bar() {}
    default void baz() {}
}
