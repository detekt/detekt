@file:Suppress("unused", "RedundantVisibilityModifier")

package cases

internal class NestedClassesVisibilityPositive {

    // reports 1 - explicit public visibility
    public interface NestedPublicInterface

    // reports 1 - explicit public visibility
    public object A

    // reports 1 - explicit public visibility
    public class NestedClassWithNestedCLass {

        // classes with a nesting depth higher than 1 are excluded
        public class NestedClass
    }
}

internal enum class NestedEnumsVisibility {

    A;

    public class Inner // reports 1 - explicit public visibility inside enum class
}
