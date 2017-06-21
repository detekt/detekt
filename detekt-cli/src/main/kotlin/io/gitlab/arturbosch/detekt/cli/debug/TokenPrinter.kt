package io.gitlab.arturbosch.detekt.cli.debug

import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.TokenRule
import io.gitlab.arturbosch.detekt.cli.print
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
class TokenPrinter : TokenRule() {

	override val issue = Issue("TokenPrinter", Severity.Minor, "")

	override fun procedure(node: ASTNode) {
		node.print()
	}

}