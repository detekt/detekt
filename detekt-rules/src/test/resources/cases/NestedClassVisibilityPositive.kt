@file:Suppress("unused", "RedundantVisibilityModifier")

package cases

internal class NestedClassesVisibilityPositive {

	// reports 1 - public visibility
	public class NestedPublicClass1
	// reports 1 - public visibility
	class NestedPublicClass2
	// reports 1 - public visibility
	interface NestedPublicInterface

	// reports 1 - public visibility
	object A

	// reports 1 - public visibility
	public class NestedClassWithNestedCLass {

		// classes with a nesting depth higher than 1 are excluded
		public class NestedClass
	}
}

internal enum class NestedEnumsVisibility {

	A;

	class Inner // reports 1 - public visibility inside enum class
}
