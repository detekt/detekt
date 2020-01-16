@file:Suppress("unused")

package cases

internal class NestedClassVisibilityNegative1 {

    class Nested1
    internal class Nested2

    // enums with public visibility are excluded
    enum class NestedEnum { One, Two }

    private interface PrivateTest
    internal interface InternalTest

    // should not detect companion object
    public companion object C
}

internal class NestedClassVisibilityNegative2 {
    // should not detect companion object
    companion object C
}

private class PrivateClassWithNestedElements {

    class Inner
}

internal interface IgnoreNestedClassInInterface {

    class Nested
}
