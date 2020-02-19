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
    private val excludes: SplitPattern
) {

    private var resolvedAnnotations = root.importList
        ?.imports
        ?.asSequence()
        ?.filterNot { it.isAllUnder }
        ?.mapNotNull { it.importedFqName?.asString() }
        ?.map { it.substringAfterLast('.') to it }
        ?.toMap() ?: emptyMap()

    /**
     * Is true if any given annotation name is declared in the SplitPattern
     * which basically describes entries to exclude.
     */
    fun shouldExclude(annotations: List<KtAnnotationEntry>): Boolean =
        annotations.firstOrNull(::isExcluded) != null

    private fun isExcluded(annotation: KtAnnotationEntry): Boolean {
        val annotationText = annotation.typeReference?.text

        // We check if resolvedAnnotations for annotation both in the keys and in the
        // values set to catch usages of fully qualified annotations 
        // (eg. @Module and @dagger.Module).
        return when {
            resolvedAnnotations.containsKey(annotationText) -> {
                resolvedAnnotations[annotationText]?.let { excludes.contains(it) } ?: false
            }
            resolvedAnnotations.containsValue(annotationText) -> {
                excludes.contains(annotationText)
            }
            else -> {
                false
            }
        }
    }
}
