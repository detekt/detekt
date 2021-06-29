package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.internal.config
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.diagnostics.WhenMissingCase
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/*
 * Based on code from Kotlin compiler:
 * https://github.com/JetBrains/kotlin/blob/v1.3.30/compiler/frontend/src/org/jetbrains/kotlin/cfg/ControlFlowInformationProvider.kt
 */
/**
 * Turn on this rule to flag `when` expressions that do not check that all cases are covered when the subject is an enum
 * or sealed class and the `when` expression is used as a statement.
 *
 * When this happens it's unclear what was intended when an unhandled case is reached. It is better to be explicit and
 * either handle all cases or use a default `else` statement to cover the unhandled cases.
 *
 * <noncompliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * fun whenOnEnumFail(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         Color.GREEN -> {}
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * fun whenOnEnumCompliant(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         Color.GREEN -> {}
 *         Color.RED -> {}
 *     }
 * }
 *
 * fun whenOnEnumCompliant2(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         else -> {}
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.2.0")
@RequiresTypeResolution
class MissingWhenCase(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "MissingWhenCase",
        Severity.Defect,
        "Check usage of `when` used as a statement and don't compare all enum or sealed class cases.",
        Debt.TWENTY_MINS
    )

    @Configuration("whether `else` can be treated as a valid case for enums and sealed classes")
    private val allowElseExpression: Boolean by config(true)

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return
        if (allowElseExpression && expression.elseExpression != null) return
        checkMissingWhenExpression(expression)
    }

    private fun checkMissingWhenExpression(expression: KtWhenExpression) {
        if (expression.isUsedAsExpression(bindingContext)) return
        val subjectExpression = expression.subjectExpression ?: return

        val subjectType = subjectExpression.getType(bindingContext)
        val enumClassDescriptor = WhenChecker.getClassDescriptorOfTypeIfEnum(subjectType)
        val sealedClassDescriptor = WhenChecker.getClassDescriptorOfTypeIfSealed(subjectType)
        if (enumClassDescriptor != null || sealedClassDescriptor != null) {
            val missingCases = WhenChecker.getMissingCases(expression, bindingContext)
            reportMissingCases(missingCases, expression)
        }
    }

    private fun reportMissingCases(
        missingCases: List<WhenMissingCase>,
        expression: KtWhenExpression
    ) {
        if (missingCases.isNotEmpty()) {
            var message = "When expression is missing cases: ${missingCases.joinToString()}."
            if (allowElseExpression) {
                message += " Either add missing cases or a default `else` case."
            }
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }
}
