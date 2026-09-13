package com.example.delegation;

public interface JavaGenericInterface<T> {
    T required();

    /** Default whose signature mentions T, so it gets substituted. */
    default T substituted(T input) { return input; }

    /** Default whose signature does not mention T. */
    default String unsubstituted() { return "JAVA_DEFAULT"; }
}
