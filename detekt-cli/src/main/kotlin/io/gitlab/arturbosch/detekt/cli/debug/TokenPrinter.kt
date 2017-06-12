package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.cli.print
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
class TokenPrinter : TokenRule("TokenPrinter") {

	override fun procedure(context: Context, node: ASTNode) {
		node.print()
	}

}