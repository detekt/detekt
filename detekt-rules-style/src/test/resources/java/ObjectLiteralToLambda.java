public interface ObjectLiteralToLambda {

    @FunctionalInterface
    interface SamWithDefaultMethods {
        void foo();
        default void bar() {}
        default void baz() {}
    }

    interface OnlyDefaultMethods {
        default void foo() {}
        default void bar() {}
        default void baz() {}
    }
}
