package com.example.delegation;

public interface JavaVarargDefault {
    void required();

    default void log(String... args) {}
}
