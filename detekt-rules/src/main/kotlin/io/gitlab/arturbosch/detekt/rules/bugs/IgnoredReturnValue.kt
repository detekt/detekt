package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

/**
 * The Kotlin compiler gives no warning for when a function which returns a value is called but its returned
 * value is ignored.  This rule warns on instances where a function returns a value but that value is not
 * used in any way.
 *
 * fun returnsValue() = 42
 * fun returnsNoValue() {}
 *
 * <noncompliant>
 *     returnsValue()
 * </noncompliant>
 *
 * <compliant>
 *     if (42 == returnsValue()) {}
 *     val x = returnsValue()
 * </compliant>
 */
class IgnoredReturnValue(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "IgnoredReturnValue",
        Severity.Defect,
        "This call returns a value which has been ignored",
        Debt.TWENTY_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        println("Call expression: ${expression.text}")

        if (bindingContext == BindingContext.EMPTY) return
        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val returnType = resolvedCall.resultingDescriptor.returnType ?: return
        if (returnType.isUnit()) {
            return
        }
        // If this is part of a binary expression, then its return value is being checked
        if (expression.parent is KtBinaryExpression) return

        report(CodeSmell(issue, Entity.from(expression), ""))
    }
}
