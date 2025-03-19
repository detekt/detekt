package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * Requires that the equals sign, when used for an expression style function, is on the same line as the
 * rest of the function signature.
 *
 * <noncompliant>
 * fun stuff(): Int
 *     = 5
 *
 * fun <V> foo(): Int where V : Int
 *     = 5
 * </noncompliant>
 *
 * <compliant>
 * fun stuff() = 5
 *
 * fun stuff() =
 *     foo.bar()
 *
 * fun <V> foo(): Int where V : Int = 5
 * </compliant>
 */
class EqualsOnSignatureLine(config: Config) : Rule(
    config,
    MESSAGE
) {

    override fun visitNamedFunction(function: KtNamedFunction) {
        val equalsToken = function.equalsToken ?: return
        val hasLineBreakBeforeEqualsToken = equalsToken
            .siblings(forward = false, withItself = false)
            .takeWhile { it is PsiWhiteSpace || it is PsiComment }
            .any { it is PsiWhiteSpace && it.textContains('\n') }
        if (hasLineBreakBeforeEqualsToken) {
            report(Finding(Entity.from(equalsToken), MESSAGE))
        }
    }

    private companion object {
        const val MESSAGE = "Equals signs for expression style functions should be on the same line as the signature."
    }
}
