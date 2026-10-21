package com.example.delegation;

public interface JavaInterfaceWithDefaults {
    void required();

    default void optional() {}
}
