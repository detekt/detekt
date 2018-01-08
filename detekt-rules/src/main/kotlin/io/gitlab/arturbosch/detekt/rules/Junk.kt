package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = this as? KtBlockExpression

fun KtClass.isDataClass() = this.modifierList?.hasModifier(KtTokens.DATA_KEYWORD) == true

fun KtClassOrObject.isObjectOfAnonymousClass() =
		this.getNonStrictParentOfType(KtObjectDeclaration::class.java) != null && this.name == null

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
