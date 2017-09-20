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
	 * Is this rule specified as active in configuration?
	 */
	val active get() = valueOrDefault("active", true)

	override fun subConfig(key: String): Config
			= config.subConfig(id).subConfig(key)

	override fun <T : Any> valueOrDefault(key: String, default: T)
			= config.subConfig(id).valueOrDefault(key, default)

}
