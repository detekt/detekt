package com.example.delegation;

public interface JavaArrayOverloads {
    void required();

    default void put(String[] values) {}

    default void put(Integer[] values) {}
}
