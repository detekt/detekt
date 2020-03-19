package io.gitlab.arturbosch.detekt.api

/**
 * A code smell indicates any possible design problem inside a program's source code.
 * The type of a code smell is described by an [Issue].
 *
 * If the design problem results from metric violations, a list of [Metric]'s
 * can describe further the kind of metrics.
 *
 * If the design problem manifests by different source locations, references to these
 * locations can be stored in additional [Entity]'s.
 */
open class CodeSmell(
    final override val issue: Issue,
    override val entity: Entity,
    override val message: String,
    override val metrics: List<Metric> = listOf(),
    override val references: List<Entity> = listOf()
) : Finding {

    override val id: String = issue.id

    override fun compact(): String = "$id - ${entity.compact()}"

    override fun compactWithSignature(): String = compact() + " - Signature=" + entity.signature

    override fun toString(): String {
        return "CodeSmell(issue=$issue, " +
            "entity=$entity, " +
            "message=$message, " +
            "metrics=$metrics, " +
            "references=$references, " +
            "id='$id')"
    }

    override fun messageOrDescription(): String = if (message.isEmpty()) issue.description else message
}

/**
 * Represents a code smell for that can be auto corrected.
 *
 * @see CodeSmell
 */
open class CorrectableCodeSmell(
    issue: Issue,
    entity: Entity,
    message: String,
    metrics: List<Metric> = listOf(),
    references: List<Entity> = listOf(),
    val autoCorrectEnabled: Boolean
) : CodeSmell(
    issue,
    entity,
    message,
    metrics,
    references
) {
    override fun toString(): String {
        return "CorrectableCodeSmell(" +
            "autoCorrectEnabled=$autoCorrectEnabled," +
            "issue=$issue, " +
            "entity=$entity, " +
            "message=$message, " +
            "metrics=$metrics, " +
            "references=$references, " +
            "id='$id')"
    }
}

/**
 * Represents a code smell for which a specific metric can be determined which is responsible
 * for the existence of this rule violation.
 *
 * @see CodeSmell
 */
open class ThresholdedCodeSmell(
    issue: Issue,
    entity: Entity,
    val metric: Metric,
    message: String,
    references: List<Entity> = emptyList()
) : CodeSmell(
    issue, entity, message, metrics = listOf(metric), references = references
) {

    val value: Int
        get() = metric.value
    val threshold: Int
        get() = metric.threshold

    override fun compact(): String = "$id - $metric - ${entity.compact()}"

    override fun messageOrDescription(): String = if (message.isEmpty()) issue.description else message
}
