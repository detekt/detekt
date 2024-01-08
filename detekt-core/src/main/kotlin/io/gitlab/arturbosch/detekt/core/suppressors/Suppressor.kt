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

private fun buildSuppressors(rule: Rule, bindingContext: BindingContext): List<Suppressor> {
    return listOfNotNull(
        annotationSuppressorFactory(rule, bindingContext),
        functionSuppressorFactory(rule, bindingContext),
    )
}

internal fun getSuppressors(rule: Rule, bindingContext: BindingContext): List<Suppressor> {
    return when (rule) {
        is Rule -> buildSuppressors(rule, bindingContext)
        else -> emptyList()
    }
}
