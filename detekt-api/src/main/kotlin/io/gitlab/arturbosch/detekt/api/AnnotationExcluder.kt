package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile

/**
 * Primary use case for an AnnotationExcluder is to decide if a KtElement should be
 * excluded from further analysis. This is done by checking if a special annotation
 * is present over the element.
 *
 * @author Niklas Baudy
 * @author Artur Bosch
 * @author schalkms
 */
class AnnotationExcluder(
		root: KtFile,
		private val excludes: SplitPattern) {

	private var resolvedAnnotations = root.importList
			?.imports
			?.asSequence()
			?.filterNot { it.isAllUnder }
			?.mapNotNull { it.importedFqName?.asString() }
			?.map { Pair(it.substringAfterLast('.'), it) }
			?.toMap()

	/**
	 * Is true if any given annotation name is declared in the SplitPattern
	 * which basically describes entries to exclude.
	 */
	fun shouldExclude(annotations: List<KtAnnotationEntry>) =
			annotations.firstOrNull(::isExcluded) != null

	private fun isExcluded(annotation: KtAnnotationEntry): Boolean =
			resolvedAnnotations?.get(annotation.typeReference?.text)
					?.let { excludes.contains(it) } ?: false
}
