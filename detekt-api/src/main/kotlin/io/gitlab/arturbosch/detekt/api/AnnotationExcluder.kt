package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile

/**
 * Primary use case for an AnnotationExcluder is to decide if a KtElement should be
 * excluded from further analysis. This is done by checking if a special annotation
 * is present over the element.
 */
class AnnotationExcluder(
    root: KtFile,
    private val excludes: List<String>
) {

    private val resolvedAnnotations = root.importList?.run {
        imports
            .asSequence()
            .filterNot { it.isAllUnder }
            .mapNotNull { it.importedFqName?.asString() }
            .map { it.substringAfterLast('.') to it }
            .toMap()
    } ?: emptyMap()

    constructor(root: KtFile, excludes: SplitPattern) : this(root, excludes.mapAll { it })

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean =
        annotations.firstOrNull(::isExcluded) != null

    private fun isExcluded(annotation: KtAnnotationEntry): Boolean {
        val annotationText = annotation.typeReference?.text
        val value = resolvedAnnotations[annotationText] ?: annotationText
        return if (value == null) false else excludes.any { value.contains(it, ignoreCase = true) }
    }
}
