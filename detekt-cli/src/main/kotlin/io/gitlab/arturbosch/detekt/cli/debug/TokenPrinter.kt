package io.gitlab.arturbosch.detekt.cli.debug

import com.intellij.lang.ASTNode
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.cli.print

/**
 * @author Artur Bosch
 */
class TokenPrinter : TokenRule("TokenPrinter") {

	override fun procedure(node: ASTNode) {
		node.print()
	}

}