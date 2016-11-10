package io.gitlab.arturbosch.detekt.api

/**
 * Basic extension of a rule by specifying the severity as a code smell.
 * @author Artur Bosch
 */
abstract class CodeSmellRule(id: String, config: Config) : Rule(id, Rule.Severity.CodeSmell, config)

/**
 * Provides a threshold attribute for this rule, which is specified manually for default values
 * but can be also obtained from within a configuration object.
 */
abstract class CodeSmellThresholdRule(id: String, config: Config, threshold: Int) : CodeSmellRule(id, config) {
	/**
	 * The used threshold for this rule is loaded from the configuration or used from the constructor value.
	 */
	protected val threshold = withConfig { valueOrDefault("threshold") { threshold } }
}