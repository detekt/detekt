package io.gitlab.arturbosch.detekt.core.suppressors

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.ConfigAware
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext

private class IgnoreAnnotationSuppressor(
    private val ignoreAnnotations: List<Regex>,
    private val bindingContext: BindingContext,
    private val shouldContainAnnotation: Boolean
) : Suppressor {

    override fun shouldSuppress(finding: Finding): Boolean {
        val element = finding.entity.ktElement
        return element != null && !shouldContainAnnotation == element.isAnnotatedWith(
            AnnotationExcluder(element.containingKtFile, ignoreAnnotations, bindingContext),
        )
    }
}

/**
 * Suppress all the issues that are raised under a code that is annotated with the annotations defined at
 * `ignoreAnnotated`.
 *
 * @config ignoreAnnotated: List<String> The annotations can be defined just by its name or with its fully qualified
 * name. If you don't run detekt with type solving the fully qualified name does not work.
 */
internal fun ignoreAnnotationSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): Suppressor? {
    return annotationSuppressorFactory(rule, bindingContext, "ignoreAnnotated", false)
}

/**
 * Suppress all the issues that are raised under a code that is not annotated with the annotations defined at
 * `ignoreAnnotated`.
 *
 * @config ignoreAnnotated: List<String> The annotations can be defined just by its name or with its fully qualified
 * name. If you don't run detekt with type solving the fully qualified name does not work.
 */
internal fun onlyAnnotationSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): Suppressor? {
    return annotationSuppressorFactory(rule, bindingContext, "onlyAnnotated", true)
}

/**
 * Order of returned elements is important, `onlyAnnotated` should be applied before `ignoreAnnotated`.
 * This way you can additionally suppress findings already suppressed by `onlyAnnotated`.
 *
 * @return list of [Suppressor]s suppressing rules based on `ignoreAnnotated` and `onlyAnnotated` config fields
 */
internal fun annotationSuppressorFactory(rule: ConfigAware, bindingContext: BindingContext): List<Suppressor> =
    listOfNotNull(
        onlyAnnotationSuppressorFactory(rule, bindingContext),
        ignoreAnnotationSuppressorFactory(rule, bindingContext)
    )

private fun annotationSuppressorFactory(
    rule: ConfigAware,
    bindingContext: BindingContext,
    configField: String,
    shouldContain: Boolean
): Suppressor? {
    val annotations = extractAnnotationRegex(rule, configField)
    return if (annotations.isNotEmpty()) {
        IgnoreAnnotationSuppressor(annotations, bindingContext, shouldContain)
    } else {
        null
    }
}

private fun extractAnnotationRegex(rule: ConfigAware, field: String) =
    rule.valueOrDefault(field, emptyList<String>()).map {
        it.qualifiedNameGlobToRegex()
    }

private fun KtElement.isAnnotatedWith(excluder: AnnotationExcluder): Boolean {
    return if (this is KtAnnotated && excluder.shouldExclude(annotationEntries)) {
        true
    } else {
        getStrictParentOfType<KtAnnotated>()?.isAnnotatedWith(excluder) ?: false
    }
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
