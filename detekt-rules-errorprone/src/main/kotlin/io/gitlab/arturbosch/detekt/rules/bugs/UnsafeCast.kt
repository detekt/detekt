package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.resolve.BindingContext

/**
 * Reports casts that will never succeed.
 *
 * <noncompliant>
 * fun foo(s: String) {
 *     println(s as Int)
 * }
 *
 * fun bar(s: String) {
 *     println(s as? Int)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(s: Any) {
 *     println(s as Int)
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class UnsafeCast(config: Config = Config.empty) : Rule(config) {

    override val defaultRuleIdAliases: Set<String> = setOf("UNCHECKED_CAST")

    override val issue: Issue = Issue("UnsafeCast",
            Severity.Defect,
            "Cast operator throws an exception if the cast is not possible.",
            Debt.TWENTY_MINS)

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        if (bindingContext == BindingContext.EMPTY) return

        if (bindingContext.diagnostics.forElement(expression.operationReference)
                .any { it.factory == Errors.CAST_NEVER_SUCCEEDS }
        ) {
            report(CodeSmell(issue, Entity.from(expression),
                    "${expression.left.text} cast to ${expression.right?.text ?: ""} cannot succeed."))
        }

        super.visitBinaryWithTypeRHSExpression(expression)
    }
}
