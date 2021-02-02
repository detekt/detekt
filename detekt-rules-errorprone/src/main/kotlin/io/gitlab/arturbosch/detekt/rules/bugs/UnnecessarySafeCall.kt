package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.resolve.BindingContext

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
@RequiresTypeResolution
@ActiveByDefault(since = "1.16.0")
class UnnecessarySafeCall(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UnnecessarySafeCall",
        Severity.Defect,
        "Unnecessary safe call operator detected.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

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
                    issue,
                    Entity.from(expression),
                    "${expression.text} contains an unnecessary " +
                        "safe call operator"
                )
            )
        }
    }
}
