package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import java.util.ArrayDeque

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

inline fun <reified T : KtElement> KtElement.parentOfType(strict: Boolean = true) =
    parentsOfTypeUntil<T, KtFile>(strict).firstOrNull()

@Suppress("USELESS_CAST", "UnsafeCast") // @BuilderInference is not smart enough
inline fun <reified T : KtElement> KtElement.collectByType(): Sequence<T> = sequence {
    val stack = ArrayDeque<PsiElement>()
    var current: PsiElement? = this@collectByType
    while (current != null) {
        if (current is T) {
            yield(current as T)
        }
        stack.addAll(current.children.toList())
        current = stack.pollFirst()
    }
}
