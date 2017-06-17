package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.cli.print
import org.jetbrains.kotlin.psi.KtElement

/**
 * @author Artur Bosch
 */
class ElementPrinter : Rule("ElementPrinter") {

	override fun visitKtElement(context: Context, element: KtElement) {
		element.print()
	}

}