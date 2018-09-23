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
	 * Wrapped configuration of the ruleSet this rule is in.
	 * Use #valueOrDefault function to retrieve properties specified for the rule
	 * implementing this interface instead.
	 * Only use this property directly if you need a specific rule set property.
	 */
	val ruleSetConfig: Config

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
	 * For auto correction to work the rule set itself enable it.
	 */
	val autoCorrect: Boolean
		get() = valueOrDefault("autoCorrect", false) &&
				ruleSetConfig.valueOrDefault("autoCorrect", true)

	/**
	 * Is this rule specified as active in configuration?
	 * If an rule is not specified in the underlying configuration, we assume it should not be run.
	 */
	val active get() = valueOrDefault("active", false)

	override fun subConfig(key: String): Config =
			ruleSetConfig.subConfig(id).subConfig(key)

	override fun <T : Any> valueOrDefault(key: String, default: T) =
			ruleSetConfig.subConfig(id).valueOrDefault(key, default)

	override fun <T : Any> valueOrNull(key: String): T? =
			ruleSetConfig.subConfig(id).valueOrNull(key)
}
