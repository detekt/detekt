package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.cli.print
import org.jetbrains.kotlin.com.intellij.psi.PsiElement

/**
 * @author Artur Bosch
 */
class ElementPrinter : Rule("ElementPrinter") {

	override fun visitElement(element: PsiElement) {
		element.print()
	}

}