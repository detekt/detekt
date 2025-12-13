package com.example.ignore_return_value;

public class Foo {
    @CheckReturnValue
    public int foo() {
        return 1;
    }
}
