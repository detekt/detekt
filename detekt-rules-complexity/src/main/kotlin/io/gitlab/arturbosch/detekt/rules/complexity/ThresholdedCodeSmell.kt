package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Entity

/**
 * Represents a code smell for which a specific metric can be determined which is responsible
 * for the existence of this rule violation.
 *
 * @see CodeSmell
 */
open class ThresholdedCodeSmell(
    entity: Entity,
    val metric: Metric,
    message: String,
    references: List<Entity> = emptyList()
) : CodeSmell(
    entity,
    message,
    references = references
)
