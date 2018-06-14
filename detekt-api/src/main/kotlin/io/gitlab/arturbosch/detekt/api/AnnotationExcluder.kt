package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile

class AnnotationExcluder(
		root: KtFile,
		private val excludes: SplitPattern) {

	private var resolvedAnnotations = root.importList
			?.imports
			?.filterNot { it.isAllUnder }
			?.mapNotNull { it.importedFqName?.asString() }
			?.map { Pair(it.split(".").last(), it) }
			?.toMap()

	fun shouldExclude(annotations: List<KtAnnotationEntry>) =
			annotations.mapNotNull { resolvedAnnotations?.get(it.typeReference?.text) }
					.any { excludes.contains(it) }
}
