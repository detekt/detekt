package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.preprocessor.typeReferenceName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement

/**
 * @author Artur Bosch
 */

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 */
fun KtElement.isSuppressedBy(id: String): Boolean {
	return this is KtAnnotated && this.isSuppressedBy(id)
}

/**
 * Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.
 */
fun KtAnnotated.isSuppressedBy(id: String): Boolean {
	return annotationEntries.find { it.typeReferenceName == "SuppressWarnings" }
			?.valueArguments
			?.find { it.getArgumentExpression()?.text?.replace("\"", "") == id } != null
}
