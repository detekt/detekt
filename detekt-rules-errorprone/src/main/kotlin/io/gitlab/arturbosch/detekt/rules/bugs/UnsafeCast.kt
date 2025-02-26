package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS

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
 */
@ActiveByDefault(since = "1.16.0")
@Alias("UNCHECKED_CAST")
class UnsafeCast(config: Config) :
    Rule(
        config,
        "Cast operator throws an exception if the cast is not possible."
    ),
    RequiresFullAnalysis {

    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        if (bindingContext.diagnostics.forElement(expression.operationReference)
                .any { it.factory == Errors.CAST_NEVER_SUCCEEDS }
        ) {
            report(
                Finding(
                    Entity.from(expression),
                    "${expression.left.text} cast to ${expression.right?.text.orEmpty()} cannot succeed."
                )
            )
        }

        super.visitBinaryWithTypeRHSExpression(expression)
    }
}
