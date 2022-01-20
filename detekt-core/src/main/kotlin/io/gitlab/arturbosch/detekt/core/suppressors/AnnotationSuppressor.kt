package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Suppress all the issues that are raised under a code that is annotated with the annotations defined at
 * `ignoreAnnotated`.
 *
 * @config ignoreAnnotated: List<String> The annotations can be defined just by its name or with its fully qualified
 * name. If you don't run detekt with type solving the fully qualified name does not work.
 */
internal fun annotationSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): Suppressor? {
    val annotations = rule.valueOrDefault("ignoreAnnotated", emptyList<String>()).map {
        it.qualifiedNameGlobToRegex()
    }
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
    annotations: List<Regex>,
    bindingContext: BindingContext
): Boolean {
    return element.isAnnotatedWith(annotations, bindingContext)
}

@Suppress("ReturnCount")
private fun KtElement.isAnnotatedWith(annotationNames: Iterable<Regex>, bindingContext: BindingContext): Boolean {
    if (this is KtAnnotated) {
        val references = annotationEntries.mapNotNull { it.typeReference }
        if (references.any { it.text in annotationNames }) {
            return true
        } else if (bindingContext != BindingContext.EMPTY) {
            if (references.any { it.fqNameOrNull(bindingContext)?.toString() in annotationNames }) {
                return true
            }
        }
    }
    return getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(annotationNames, bindingContext) ?: false
}

private fun KtTypeReference.fqNameOrNull(bindingContext: BindingContext): FqName? {
    return bindingContext[BindingContext.TYPE, this]?.fqNameOrNull()
}

private operator fun Iterable<Regex>.contains(a: String?): Boolean {
    if (a == null) return false
    return any { it.matches(a) }
}

private fun String.qualifiedNameGlobToRegex(): Regex {
    return this
        .replace(".", """\.""")
        .replace("**", "//")
        .replace("*", "[^.]*")
        .replace("//", ".*")
        .replace("?", ".")
        .toRegex()
}
