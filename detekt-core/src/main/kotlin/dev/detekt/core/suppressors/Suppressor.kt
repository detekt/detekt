package dev.detekt.core.suppressors

import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.tooling.api.AnalysisMode

fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}

internal fun buildSuppressors(rule: Rule, analysisMode: AnalysisMode): List<Suppressor> = listOfNotNull(
    annotationSuppressorFactory(rule, analysisMode),
    functionSuppressorFactory(rule, analysisMode),
)
