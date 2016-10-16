package io.gitlab.arturbosch.detekt.core.debug

import com.intellij.lang.ASTNode
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.print

/**
 * @author Artur Bosch
 */
class TokenPrinter : TokenRule("TokenPrinter") {

	override fun procedure(node: ASTNode) {
		node.print()
	}

}