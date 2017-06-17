package io.gitlab.arturbosch.detekt.api

/**
 * Provides a threshold attribute for this rule, which is specified manually for default values
 * but can be also obtained from within a configuration object.
 */
abstract class ThresholdRule(id: String, config: Config, threshold: Int) : Rule(id, config) {
	/**
	 * The used threshold for this rule is loaded from the configuration or used from the constructor value.
	 */
	protected val threshold = withConfig { valueOrDefault("threshold", threshold) }
}