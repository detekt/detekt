package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
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
class EqualsOnSignatureLine(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Style, MESSAGE, Debt.FIVE_MINS)

    override fun visitNamedFunction(function: KtNamedFunction) {
        val equals = function.equalsToken ?: return

        // Get the tokens after the parameter list (but not including)
        val afterParams = function.valueParameterList?.siblings(forward = true, withItself = false) ?: return

        // Collect tokens until we find the equals sign
        val untilEquals = afterParams.takeWhile { it !== equals }.toList()

        // Walk backwards from the '=' to find the whitespace right behind it
        val whitespace = untilEquals.takeLastWhile { it is PsiWhiteSpace }

        // Search the whitespace tokens to see if they contain newlines
        whitespace.forEach {
            if (it.textContains('\n')) {
                report(CodeSmell(issue, Entity.from(equals), MESSAGE))
            }
        }
    }

    private companion object {
        const val MESSAGE = "Equals signs for expression style functions should be on the same line as the signature"
    }
}
