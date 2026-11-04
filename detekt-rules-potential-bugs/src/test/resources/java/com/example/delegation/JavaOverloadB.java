package com.example.delegation;

public interface JavaOverloadB {
    void requiredB();

    default void foo(String s) {}
}
