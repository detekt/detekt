package com.example.delegation;

public interface JavaDeprecatedOverloads {
    void required();

    default void write(int number) {}

    @Deprecated
    default void write(Integer number) {}
}
