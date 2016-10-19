package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */

abstract class CodeSmellRule(id: String, config: Config) : Rule(id, Rule.Severity.CodeSmell, config)

abstract class CodeSmellThresholdRule(id: String, config: Config, threshold: Int) : CodeSmellRule(id, config) {
	protected val threshold = config.valueOrDefault("threshold") { threshold }
}