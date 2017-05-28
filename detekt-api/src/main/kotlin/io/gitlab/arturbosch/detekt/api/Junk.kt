package io.gitlab.arturbosch.detekt.api

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import kotlin.reflect.KClass

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

/**
 * When analyzing sub path 'testData' of the kotlin project, CompositeElement.getText() throws
 * a RuntimeException stating 'Underestimated text length' - #65.
 */
fun withPsiTextRuntimeError(defaultValue: () -> String, block: () -> String): String {
	return try {
		block()
	} catch (e: RuntimeException) {
		val message = e.message
		if (message != null && message.contains("Underestimated text length")) {
			return defaultValue() + "!<UnderestimatedTextLengthException>"
		} else throw e
	}
}

/**
 * Tests if this element is part of given PsiElement.
 */
fun PsiElement.isPartOf(clazz: KClass<out PsiElement>) = getNonStrictParentOfType(clazz.java) != null

/**
 * Tests of this element is part of a kotlin string.
 */
fun PsiElement.isPartOfString() = isPartOf(KtStringTemplateEntry::class)
