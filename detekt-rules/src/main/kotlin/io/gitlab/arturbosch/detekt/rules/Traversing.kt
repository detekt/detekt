package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

/**
 * Returns a list of all parents of type [T] before first occurrence of [S].
 */
@Suppress("UnsafeCast")
inline fun <reified T : KtElement, reified S : KtElement> KtElement.parentsOfType(): Sequence<T> = sequence {
	var current: PsiElement? = this@parentsOfType
	while (current != null && !S::class.isInstance(current)) {
		if (T::class.isInstance(current)) {
			yield(current as T)
		}
		current = current.parent
	}
}
