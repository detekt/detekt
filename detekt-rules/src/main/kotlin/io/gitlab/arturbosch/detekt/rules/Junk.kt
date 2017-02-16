package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * @author Artur Bosch
 */

fun KtExpression?.asBlockExpression(): KtBlockExpression? = if (this is KtBlockExpression) this else null

fun KtModifierListOwner.isPublicNotOverriden() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)
		|| (this.hasModifier(KtTokens.PRIVATE_KEYWORD) || this.hasModifier(KtTokens.PROTECTED_KEYWORD)
		|| this.hasModifier(KtTokens.INTERNAL_KEYWORD) || this.hasModifier(KtTokens.OVERRIDE_KEYWORD)).not()

fun KtCallExpression.isUsedForNesting(): Boolean = when (getCallNameExpression()?.text) {
	"run", "let", "apply", "with", "use", "forEach" -> true
	else -> false
}

inline fun <K, V> MutableMap<K, V>.merge(key: K, value: V, mergeFunction: (V, V) -> V) {
	val oldValue = this[key]
	if (oldValue == null) {
		this.put(key, value)
	} else {
		this.put(key, mergeFunction(oldValue, value))
	}
}

inline fun <reified T : KtElement> KtNamedFunction.collectByType(): List<T> {
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
