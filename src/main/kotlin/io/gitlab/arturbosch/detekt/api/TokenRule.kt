package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode

/**
 * @author Artur Bosch
 */
abstract class TokenRule(id: String) : Rule(id) {

	override fun visit(root: ASTNode) {
		preVisit(root)
		root.visitTokens { procedure(it) }
		postVisit(root)
	}

	abstract fun procedure(node: ASTNode): Unit
}