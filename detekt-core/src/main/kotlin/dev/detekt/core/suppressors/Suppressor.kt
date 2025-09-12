package dev.detekt.core.suppressors

import dev.detekt.api.Finding
import dev.detekt.api.Rule

fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}

internal fun buildSuppressors(rule: Rule): List<Suppressor> =
    listOfNotNull(
        annotationSuppressorFactory(rule),
        functionSuppressorFactory(rule),
    )
