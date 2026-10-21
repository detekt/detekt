package com.example.delegation;

public interface JavaInterfaceWithPrivate {
    void required();

    private void helper() {}

    default void run() {
        helper();
    }
}
