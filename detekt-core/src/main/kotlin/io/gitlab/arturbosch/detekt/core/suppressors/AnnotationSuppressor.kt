package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.ConfigAware
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

internal fun annotationSuppressorFactory(rule: ConfigAware): Suppressor? {
    val annotations = rule.valueOrDefault("ignoreAnnotated", emptyList<String>())
    return if (annotations.isNotEmpty()) {
        Suppressor { finding ->
            val element = finding.entity.ktElement
            element != null && annotationSuppressor(element, annotations)
        }
    } else {
        null
    }
}

private fun annotationSuppressor(element: KtElement, annotations: List<String>): Boolean {
    return element.isAnnotatedWith(annotations)
}

private fun KtElement.isAnnotatedWith(annotationNames: Iterable<String>): Boolean {
    return if (this is KtAnnotated && annotationEntries.find { it.typeReference?.text in annotationNames } != null) {
        true
    } else {
        getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(annotationNames) ?: false
    }
}
