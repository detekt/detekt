package com.example.delegation;

public interface JavaOverloadA {
    void requiredA();

    default void foo() {}
}
