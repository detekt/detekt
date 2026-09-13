package com.example.delegation;

public interface JavaOverloadedDefaults {
    void required();

    default void write(String text) {}

    default void write(int number) {}
}
