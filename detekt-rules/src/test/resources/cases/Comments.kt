package cases

/**
 * @author Artur Bosch
 */
@Suppress("unused")
open class Comments {

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