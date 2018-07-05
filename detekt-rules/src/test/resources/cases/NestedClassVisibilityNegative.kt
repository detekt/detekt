@file:Suppress("unused")

package cases

internal class NestedClassVisibilityNegative {

	// enums with public visibility are excluded
	enum class NestedEnum { One, Two }

	private interface PrivateTest
	internal interface InternalTest

	// should not detect companion object
	companion object C
}

private class PrivateClassWithNestedElements {

	class Inner
}


