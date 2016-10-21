package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
abstract class TokenRule(id: String,
						 severity: Severity = Rule.Severity.Minor,
						 config: Config = Config.EMPTY) : Rule(id, severity, config) {

	override fun visit(root: ASTNode) {
		ifRuleActive {
			clearFindings()
			preVisit(root)
			root.visitTokens { procedure(it) }
			postVisit(root)
		}
	}

	abstract fun procedure(node: ASTNode)
}
