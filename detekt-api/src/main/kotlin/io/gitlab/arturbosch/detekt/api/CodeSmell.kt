package io.gitlab.arturbosch.detekt.api

/**
 * A code smell is a finding, implementing its behaviour. Use this class to store
 * rule violations.
 *
 * @author Artur Bosch
 */
open class CodeSmell(final override val issue: Issue,
					 override val entity: Entity,
					 override val metrics: List<Metric> = listOf(),
					 override val references: List<Entity> = listOf()) : Finding {

	override val id: String = issue.id

	override fun compact() = "$id - ${entity.compact()}"

	override fun compactWithSignature() = compact() + " - Signature=" + entity.signature
}

/**
 * Represents a code smell for which a specific metric and reference entity can be determined which are responsible
 * for the existence of this rule violation.
 */
open class CodeSmellWithReferenceAndMetric(
		issue: Issue, entity: Entity, val reference: Entity, metric: Metric) : ThresholdedCodeSmell(
		issue, entity, metric, references = listOf(reference)) {

	override fun compact() = "$id - $metric - ref=${reference.name} - ${entity.compact()}"
}

/**
 * Represents a code smell for which a specific metric can be determined which is responsible
 * for the existence of this rule violation.
 */
open class ThresholdedCodeSmell(
		issue: Issue, entity: Entity, val metric: Metric, references: List<Entity> = emptyList()) : CodeSmell(
		issue, entity, metrics = listOf(metric), references = references) {
	val value: Int
		get() = metric.value
	val threshold: Int
		get() = metric.threshold

	override fun compact() = "$id - $metric - ${entity.compact()}"
}
