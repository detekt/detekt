package io.gitlab.arturbosch.detekt.core.suppressors

import io.github.detekt.psi.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Suppress all the issues that are raised under a code that is annotated with the annotations defined at
 * `ignoreAnnotated`.
 *
 * @config ignoreAnnotated: List<String> The annotations can be defined just by its name or with its fully qualified
 * name. If you don't run detekt with type solving the fully qualified name does not work.
 */
internal fun annotationSuppressorFactory(rule: Rule, bindingContext: BindingContext): Suppressor? {
    val annotations = rule.config.valueOrDefault("ignoreAnnotated", emptyList<String>()).map {
        it.qualifiedNameGlobToRegex()
    }
    return if (annotations.isNotEmpty()) {
        if (rule.isForbiddenSuppress()) {
            warnForbiddenSuppressCannotBeSuppressed()
            return null
        }
        Suppressor { finding ->
            val element = finding.entity.ktElement
            element.isAnnotatedWith(AnnotationExcluder(element.containingKtFile, annotations, bindingContext))
        }
    } else {
        null
    }
}

private fun KtElement.isAnnotatedWith(excluder: AnnotationExcluder): Boolean =
    if (this is KtAnnotated && excluder.shouldExclude(annotationEntries)) {
        true
    } else {
        getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(excluder) ?: false
    }

private fun String.qualifiedNameGlobToRegex(): Regex =
    this
        .replace(".", """\.""")
        .replace("**", "//")
        .replace("*", "[^.]*")
        .replace("//", ".*")
        .replace("?", ".")
        .toRegex()
