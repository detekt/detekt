package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiUtil

private val identifierRegex = Regex("[aA-zZ]+([-][aA-zZ]+)*")

/**
 * Checks if given string matches the criteria of an id - [aA-zZ]+([-][aA-zZ]+)* .
 */
fun validateIdentifier(id: String) {
	require(id.matches(identifierRegex), { "id must match [aA-zZ]+([-][aA-zZ]+)*" })
}

internal fun ASTNode.visitTokens(currentNode: (node: ASTNode) -> Unit) {
	currentNode(this)
	getChildren(null).forEach { it.visitTokens(currentNode) }
}

internal fun ASTNode.visit(visitor: DetektVisitor) {
	KtPsiUtil.visitChildren(this.psi as KtElement, visitor, null)
}