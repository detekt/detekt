package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
abstract class TokenRule(id: String, severity: Severity = Rule.Severity.Minor) : Rule(id, severity) {

	override fun visit(root: ASTNode) {
		clearFindings()
		preVisit(root)
		root.visitTokens { procedure(it) }
		postVisit(root)
	}

	abstract fun procedure(node: ASTNode): Unit
}
