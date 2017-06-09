package io.gitlab.arturbosch.detekt.api

/**
 * A code smell is a finding, implementing its behaviour. Use this class to store
 * rule violations.
 *
 * @author Artur Bosch
 */
open class CodeSmell(override val id: String,
					 override val severity: Rule.Severity,
					 override val entity: Entity,
					 override val description: String = "",
					 override val metrics: List<Metric> = listOf(),
					 override val references: List<Entity> = listOf()) : Finding {

	override fun compact(): String {
		return "$id - ${entity.compact()}"
	}

	override fun compactWithSignature(): String {
		return compact() + " - Signature=" + entity.signature
	}
}

/**
 * Represents a code smell for which a specific metric and reference entity can be determined which are responsible
 * for the existence of this rule violation.
 */
open class CodeSmellWithReferenceAndMetric(
		id: String, severity: Rule.Severity, entity: Entity, val reference: Entity, metric: Metric) : ThresholdedCodeSmell(
		id, severity, entity, metric, references = listOf(reference)) {

	override fun compact(): String {
		return "$id - $metric - ref=${reference.name} - ${entity.compact()}"
	}
}

/**
 * Represents a code smell for which a specific metric can be determined which is responsible
 * for the existence of this rule violation.
 */
open class ThresholdedCodeSmell(
		id: String, severity: Rule.Severity, entity: Entity, val metric: Metric, references: List<Entity> = emptyList()) : CodeSmell(
		id, severity, entity, metrics = listOf(metric), references = references) {
	val value: Int
		get() = metric.value
	val threshold: Int
		get() = metric.threshold

	override fun compact(): String {
		return "$id - $metric - ${entity.compact()}"
	}
}
