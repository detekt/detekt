package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.BaseRule
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.resolve.BindingContext

fun interface Suppressor {
    /**
     * Given a Finding it decides if it should be suppressed (`true`) or not (`false`)
     */
    fun shouldSuppress(finding: Finding): Boolean
}

private fun buildSuppressors(rule: ConfigAware, bindingContext: BindingContext): List<Suppressor> {
    return annotationSuppressorFactory(rule, bindingContext) + listOfNotNull(
        functionSuppressorFactory(rule, bindingContext),
    )
}

internal fun getSuppressors(rule: BaseRule, bindingContext: BindingContext): List<Suppressor> {
    return when (rule) {
        is ConfigAware -> buildSuppressors(rule, bindingContext)
        else -> emptyList()
    }
}
