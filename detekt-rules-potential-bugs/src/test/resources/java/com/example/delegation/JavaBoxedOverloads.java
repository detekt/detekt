package com.example.delegation;

public interface JavaBoxedOverloads {
    void required();

    default void write(int number) {}

    default void write(Integer number) {}
}
