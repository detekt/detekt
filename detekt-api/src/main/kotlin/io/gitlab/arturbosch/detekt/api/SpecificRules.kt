package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */

abstract class MetricThresholdRule(id: String, val threshold: Int, severity: Severity = Rule.Severity.Minor) : Rule(id, severity)

abstract class MetricThresholdCodeSmellRule(id: String, threshold: Int) : MetricThresholdRule(id, threshold, Rule.Severity.CodeSmell)
