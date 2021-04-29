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
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.ErrorType

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
            // For external types, if they're not included in the classpath, we still get an Errors.UNNECESSARY_SAFE_CALL.
            // This causes false positives if our users are misconfiguring detekt with Type Resolution.
            // Here we try to check if the compiler reports failed to resolve the nullable type.

            // More reference on where the compiler is attaching this information is here:
            // https://github.com/JetBrains/kotlin/blob/29b23e79f32791e456a5b4a453277f0f0b3e984d/compiler/frontend/src/org/jetbrains/kotlin/resolve/calls/CallExpressionResolver.kt#L543
            // Specifically the Kotlin Compiler is not checking if the receiver type is of type `ErrorType`
            // so we circumvent it here.
            if (compilerReport is DiagnosticWithParameters1<*, *> && compilerReport.a is ErrorType) {
                return
            }
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
