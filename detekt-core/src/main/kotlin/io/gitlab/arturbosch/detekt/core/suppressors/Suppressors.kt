package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.internal.BaseRule

/**
 * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
 */
typealias Suppressor = (Finding) -> Boolean

internal fun getSuppressors(rule: BaseRule): List<Suppressor> {
    return if (rule is ConfigAware) {
        listOfNotNull(
            annotationSuppressorFactory(rule),
        )
    } else {
        emptyList()
    }
}
