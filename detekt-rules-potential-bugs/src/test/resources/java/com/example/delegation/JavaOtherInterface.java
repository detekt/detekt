package com.example.delegation;

public interface JavaOtherInterface {
    void alsoRequired();

    default void alsoOptional() {}
}
