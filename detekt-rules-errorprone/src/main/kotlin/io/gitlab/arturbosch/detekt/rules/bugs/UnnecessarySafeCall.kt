package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

/**
 * Reports unnecessary safe call operators (`?.`) that can be removed by the user.
 *
 * <noncompliant>
 * val a: String = ""
 * val b = a?.length
 * </noncompliant>
 *
 * <compliant>
 * val a: String? = null
 * val b = a?.length
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class UnnecessarySafeCall(config: Config) :
    Rule(
        config,
        "Unnecessary safe call operator detected."
    ),
    RequiresTypeResolution {
    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        val safeAccessElement = expression.getChildOfType<LeafPsiElement>()
        if (safeAccessElement == null || safeAccessElement.elementType != KtTokens.SAFE_ACCESS) {
            return
        }

        val compilerReport = bindingContext
            .diagnostics
            .forElement(safeAccessElement)
            .firstOrNull { it.factory == Errors.UNNECESSARY_SAFE_CALL }

        if (compilerReport != null) {
            report(
                CodeSmell(
                    Entity.from(expression),
                    "${expression.text} contains an unnecessary " +
                        "safe call operator"
                )
            )
        }
    }
}
