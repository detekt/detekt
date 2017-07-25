package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
interface Report {
	val id: String get() = javaClass.simpleName
	val priority: Int get() = -1

	@Suppress("EmptyFunctionBlock")
	fun init(config: Config) {
	}

}
