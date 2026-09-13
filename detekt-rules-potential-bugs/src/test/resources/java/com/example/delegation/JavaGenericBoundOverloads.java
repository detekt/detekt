package com.example.delegation;

public interface JavaGenericBoundOverloads {
    void required();

    default <T extends Number> void accept(T value) {}

    default <T extends CharSequence> void accept(T value) {}
}
