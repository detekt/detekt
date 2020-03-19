package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement

/**
 * Returns a list of all parents of type [T] before first occurrence of [S].
 */
inline fun <reified T : KtElement, reified S : KtElement> KtElement.parentsOfTypeUntil(strict: Boolean = true) =
    sequence<T> {
        var current: PsiElement? = if (strict) this@parentsOfTypeUntil.parent else this@parentsOfTypeUntil
        while (current != null && current !is S) {
            if (current is T) {
                yield(current)
            }
            current = current.parent
        }
    }
