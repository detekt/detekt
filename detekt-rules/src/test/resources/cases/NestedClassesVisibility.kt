@file:Suppress("unused", "RedundantVisibilityModifier")

package cases

internal class NestedClassesVisibility {

	public class NestedPublicClass1 // report
	class NestedPublicClass2 // report
	interface NestedPublicInterface // report

	// Enums are excluded
	enum class NestedEnum { One, Two }

	private interface PrivateTest
	internal interface InternalTest

	public class NestedClassWithNestedCLass { // report

		// classes with a nesting depth higher than 1 are excluded
		public class NestedClass
	}
}
