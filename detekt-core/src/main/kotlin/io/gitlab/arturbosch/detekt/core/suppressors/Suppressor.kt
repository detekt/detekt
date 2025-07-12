package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.resolve.BindingContext

fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}

internal fun buildSuppressors(rule: Rule, bindingContext: BindingContext): List<Suppressor> =
    listOfNotNull(
        annotationSuppressorFactory(rule),
        functionSuppressorFactory(rule, bindingContext),
    )
