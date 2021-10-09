package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.ConfigAware
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

internal fun annotationSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): Suppressor? {
    val annotations = rule.valueOrDefault("ignoreAnnotated", emptyList<String>())
    return if (annotations.isNotEmpty()) {
        Suppressor { finding ->
            val element = finding.entity.ktElement
            element != null && annotationSuppressor(element, annotations, bindingContext)
        }
    } else {
        null
    }
}

private fun annotationSuppressor(
    element: KtElement,
    annotations: List<String>,
    bindingContext: BindingContext
): Boolean {
    return element.isAnnotatedWith(annotations, bindingContext)
}

private fun KtElement.isAnnotatedWith(annotationNames: Iterable<String>, bindingContext: BindingContext): Boolean {
    return if (this is KtAnnotated && annotationEntries.find { it.typeReference?.text in annotationNames } != null) {
        true
    } else {
        getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(annotationNames, bindingContext) ?: false
    }
}
