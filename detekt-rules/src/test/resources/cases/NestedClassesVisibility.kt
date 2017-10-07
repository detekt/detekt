package cases

internal class ParentType {
	public class NestedType { // report
	}

	enum class NestedEnum { One, Two }

	private enum class NestedPrivateEnum { Three } // valid

	interface Test {} // report

	private interface PrivateTest {} // valid

	internal interface InternalTest {} // valid

	public class NestedClassWithNestedCLass { //report
		public class NestedClass {

		}

		public interface NestedInterface{

		}
	}
}
