package cases

import java.io.Serializable

@Suppress("unused")
open class Comments<T : K, out K> : Serializable {

	companion object {
		fun withinCompanionNeedComment(){}
	}

	public class INeedComment
	private class NoNeedForComments{}

	fun ohNoComment() {}
	/**
	 * What what what, who ever writes documentation?
	 */
	fun iHaveComment() {
		fun iDontNeedDocumentation(){}
	}
	public fun ohNoComment2(){}
	internal fun nope1(){}
	protected fun nope2(){}
	private fun nope3(){}

}