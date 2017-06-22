package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
interface ConfigAware : Config {

	/**
	 * Id which is used to retrieve the sub config for this rule.
	 */
	val id: String

	/**
	 * Wrapped configuration to use for specified id.
	 */
	val config: Config

	/**
	 * If your rule supports to automatically correct the misbehaviour of underlying smell,
	 * specify your code inside this method call, to allow the user of your rule to trigger auto correction
	 * only when needed.
	 */
	fun withAutoCorrect(block: () -> Unit) {
		if (autoCorrect) {
			block()
		}
	}

	/**
	 * Does this rule have auto correct specified in configuration?
	 */
	val autoCorrect: Boolean get() = valueOrDefault("autoCorrect", true)

	/**
	 * Is this rule specified as active in configuration?
	 */
	val active get() = valueOrDefault("active", true)

	override fun subConfig(key: String): Config
			= config.subConfig(id).subConfig(key)

	override fun <T : Any> valueOrDefault(key: String, default: T): T
			= config.subConfig(id).valueOrDefault(key, default)

}