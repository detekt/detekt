package io.gitlab.arturbosch.detekt.api

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.preprocessor.typeReferenceName
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile

/**
 * @author Artur Bosch
 */

/**
 * Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
 * If this element cannot have annotations, the first annotative parent is searched.
 */
fun KtElement.isSuppressedBy(id: String): Boolean {
	return this is KtAnnotated && this.isSuppressedBy(id) || findAnnotatedSuppressedParent(id)
}

private fun KtElement.findAnnotatedSuppressedParent(id: String): Boolean {
	val parent = PsiTreeUtil.getParentOfType(this, KtAnnotated::class.java, true)

	if (parent != null && parent !is KtFile) {
		if (parent.isSuppressedBy(id)) return true
		else return parent.findAnnotatedSuppressedParent(id)
	}

	return false
}

/**
 * Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.
 */
fun KtAnnotated.isSuppressedBy(id: String): Boolean {
	return annotationEntries.find { it.typeReferenceName.let { it == "Suppress" || it == "SuppressWarnings" } }
			?.valueArguments
			?.find { it.getArgumentExpression()?.text?.replace("\"", "") == id } != null
}
