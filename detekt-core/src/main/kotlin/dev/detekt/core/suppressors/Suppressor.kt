package dev.detekt.core.suppressors

import dev.detekt.api.Finding
import dev.detekt.api.Rule
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
