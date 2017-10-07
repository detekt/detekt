package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = this as? KtBlockExpression

fun KtModifierListOwner.isPublicNotOverridden() =
		isPublic() && !this.hasModifier(KtTokens.OVERRIDE_KEYWORD)

fun KtModifierListOwner.isPublic(): Boolean {
	return this.hasModifier(KtTokens.PUBLIC_KEYWORD)
			|| !(this.hasModifier(KtTokens.PRIVATE_KEYWORD)
			|| this.hasModifier(KtTokens.PROTECTED_KEYWORD)
			|| this.hasModifier(KtTokens.INTERNAL_KEYWORD))
}

fun KtModifierListOwner.isInternal() = this.hasModifier(KtTokens.INTERNAL_KEYWORD)

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
	"run", "let", "apply", "with", "use", "forEach" -> true
	else -> false
}

fun KtBlockExpression.hasCommentInside(): Boolean {
	val commentKey = Key<Boolean>("comment")
	this.acceptChildren(object : DetektVisitor() {
		override fun visitComment(comment: PsiComment?) {
			if (comment != null) putUserData(commentKey, true)
		}
	})
	return getUserData(commentKey) == true
}

fun KtClass.companionObject() = this.companionObjects.singleOrNull { it.isCompanion() }

inline fun <reified T : KtElement> KtElement.collectByType(): List<T> {
	val list = mutableListOf<T>()
	this.accept(object : DetektVisitor() {
		override fun visitKtElement(element: KtElement) {
			if (element is T) {
				list.add(element)
			}
			element.children.forEach { it.accept(this) }
		}
	})
	return list
}
