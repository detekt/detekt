package com.example.delegation;

public interface JavaCrossPackageOverloads {
    void required();

    default void handle(Stamp stamp) {}

    default void handle(com.example.other.Stamp stamp) {}
}
