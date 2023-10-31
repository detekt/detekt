package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue

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
    issue,
    entity,
    message,
    references = references
) {
    override fun compact(): String = "$id - $metric - ${entity.compact()}"

    override fun messageOrDescription(): String = message.ifEmpty { issue.description }
}
