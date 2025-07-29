package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.hasImplicitParameterReference
import org.jetbrains.kotlin.builtins.StandardNames.IMPLICIT_LAMBDA_PARAMETER_NAME
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Lambda expressions are very useful in a lot of cases, and they often include very small chunks of
 * code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
 * to make code more concise. However, when you are dealing with lambdas that contain multiple statements,
 * you might end up with code that is hard to read if you don't specify a readable, descriptive parameter name
 * explicitly.
 *
 * <noncompliant>
 * val digits = 1234.let {
 *     println(it)
 *     listOf(it)
 * }
 *
 * val digits = 1234.let { it ->
 *     println(it)
 *     listOf(it)
 * }
 *
 * val flat = listOf(listOf(1), listOf(2)).mapIndexed { index, it ->
 *     println(it)
 *     it + index
 * }
 * </noncompliant>
 *
 * <compliant>
 * val digits = 1234.let { explicitParameterName ->
 *     println(explicitParameterName)
 *     listOf(explicitParameterName)
 * }
 *
 * val lambda = { item: Int, that: String ->
 *     println(item)
 *     item.toString() + that
 * }
 *
 * val digits = 1234.let { listOf(it) }
 * val digits = 1234.let {
 *     listOf(it)
 * }
 * val digits = 1234.let { it -> listOf(it) }
 * val digits = 1234.let { it ->
 *     listOf(it)
 * }
 * val digits = 1234.let { explicit -> listOf(explicit) }
 * val digits = 1234.let { explicit ->
 *     listOf(explicit)
 * }
 * </compliant>
 *
 */
class MultilineLambdaItParameter(config: Config) :
    Rule(
        config,
        "Multiline lambdas should not use `it` as a parameter name."
    ),
    RequiresAnalysisApi {

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)

        if (!lambdaExpression.isMultiline()) return

        val parameterNames = lambdaExpression.valueParameters.map { it.nameAsName }
        if (IMPLICIT_LAMBDA_PARAMETER_NAME in parameterNames) {
            // Explicit `it`
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    "The parameter name in a multiline lambda should not be an explicit `it`. " +
                        "Consider giving your parameter a readable and descriptive name."
                )
            )
        } else if (parameterNames.isEmpty() && lambdaExpression.hasImplicitParameterReference()) {
            // Implicit `it`
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    "The implicit `it` should not be used in a multiline lambda. " +
                        "Consider giving your parameter a readable and descriptive name."
                )
            )
        }
    }

    private fun KtLambdaExpression.isMultiline(): Boolean {
        val statements = bodyExpression?.statements ?: return false
        return when (statements.size) {
            0 -> false
            1 -> statements.single().textContains('\n')
            else -> true
        }
    }
}
