package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Lambda expressions are are very useful in a lot of cases and they often include very small chunks of
 * code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
 * to make code more concise. However, when you are dealing with multiline lambdas, you might end up with a code
 * that is hard to read if you don't specify the parameter name explicitly.
 *
 * <noncompliant>
 * val digits = 1234.let {
 *   listOf(it)
 * }
 *
 * val digits = 1234.let { it ->
 *   listOf(it)
 * }
 *
 * val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it ->
 *   it + index
 * }
 * </noncompliant>
 *
 * <compliant>
 * val digits = 1234.let { explicitParameterName ->
 *   listOf(explicitParameterName)
 * }
 * val lambda = { item: Int, that: String ->
 *   item.toString() + that
 * }
 *
 * val digits = 1234.let { listOf(it) }
 * val digits = 1234.let { it -> listOf(it) }
 * val digits = 1234.let { explicit -> listOf(explicit) }
 * </compliant>
 */
class MultilineLambdaItParameter(val config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName, Severity.Style,
        "Multiline lambdas should not use `it` as a parameter name", Debt.FIVE_MINS
    )

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val parameterNames = lambdaExpression.valueParameters.map { it.name }
        val isMultiline = lambdaExpression.text.contains("\n")
        if (isMultiline && (parameterNames.isEmpty() || IT_LITERAL in parameterNames)) {
            report(
                CodeSmell(
                    issue, Entity.from(lambdaExpression),
                    "The parameter name in a multiline lambda should not be an implicit/explicit `it`. Consider giving your parameter a name"
                )
            )
        }
    }
}
