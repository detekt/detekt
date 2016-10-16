package io.gitlab.arturbosch.detekt.core.debug

import com.intellij.psi.PsiElement
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.print

/**
 * @author Artur Bosch
 */
class ElementPrinter : Rule("ElementPrinter") {

	override fun visitElement(element: PsiElement) {
		element.print()
	}

}