package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
@Suppress("EmptyFunctionBlock")
interface Extension {
	val id: String get() = javaClass.simpleName
	val priority: Int get() = -1

	fun init(config: Config) {}
}
