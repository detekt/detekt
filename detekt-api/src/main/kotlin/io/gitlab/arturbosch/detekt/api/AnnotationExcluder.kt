package io.gitlab.arturbosch.detekt.api

import io.github.detekt.psi.internal.FullQualifiedNameGuesser
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Primary use case for an AnnotationExcluder is to decide if a KtElement should be
 * excluded from further analysis. This is done by checking if a special annotation
 * is present over the element.
 */
class AnnotationExcluder(
    root: KtFile,
    private val excludes: List<Regex>,
    private val context: BindingContext,
) {

    private val fullQualifiedNameGuesser = FullQualifiedNameGuesser(root)

    @Deprecated("Use AnnotationExcluder(List<Regex>, KtFile) instead")
    constructor(root: KtFile, excludes: SplitPattern) : this(
        root,
        excludes.mapAll { it }
            .map { it.replace(".", "\\.").replace("*", ".*").toRegex() },
        BindingContext.EMPTY,
    )

    @Deprecated("Use AnnotationExcluder(List<Regex>, KtFile) instead")
    constructor(
        root: KtFile,
        excludes: List<String>,
    ) : this(
        root,
        excludes.map {
            it.replace(".", "\\.").replace("*", ".*").toRegex()
        },
        BindingContext.EMPTY,
    )

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean = annotations.any(::isExcluded)

    private fun isExcluded(annotation: KtAnnotationEntry): Boolean {
        val annotationText = annotation.typeReference?.text?.ifEmpty { null } ?: return false
        /*
         We can't know if the annotationText is a full-qualified name or not. We can have these cases:
         @Component
         @Component.Factory
         @dagger.Component.Factory
         For that reason we use a heuristic here: If the first character is lower case we assume it's a package name
         */
        val possibleNames = if (!annotationText.first().isLowerCase()) {
            fullQualifiedNameGuesser.getFullQualifiedName(annotationText)
        } else {
            listOf(annotationText)
        }.flatMap(::expandFqNames)
        return excludes.any { exclude -> possibleNames.any { exclude.matches(it) } }
    }
}

private fun expandFqNames(fqName: String): List<String> {
    return fqName
        .split(".")
        .dropWhile { it.first().isLowerCase() }
        .reversed()
        .scan("") { acc, name ->
            if (acc.isEmpty()) name else "$name.$acc"
        }
        .drop(1) + fqName
}
