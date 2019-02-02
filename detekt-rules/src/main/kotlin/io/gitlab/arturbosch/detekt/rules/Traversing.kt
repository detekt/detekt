package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.DetektVisitor
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

/**
 * Returns a list of all parents of type [T] before first occurrence of [S].
 */
@Suppress("UnsafeCast")
inline fun <reified T : KtElement, reified S : KtElement> KtElement.parentsOfTypeUntil() = sequence<T> {
	var current: PsiElement? = this@parentsOfTypeUntil
	while (current != null && current !is S) {
		if (current is T) {
			yield(current)
		}
		current = current.parent
	}
}

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
