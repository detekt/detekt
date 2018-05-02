@file:Suppress("unused")
package cases

import java.io.Serializable

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

internal class NoComments {

	fun nope0() {}
	public fun nope1() {}
	internal fun nope2() {}
	protected fun nope3() {}
	private fun nope4() {}

}

private class NoCommentsPlease {

	fun nope0() {}
	public fun nope1() {}
	internal fun nope2() {}
	protected fun nope3() {}
	private fun nope4() {}

}

object MissMyDocObject
