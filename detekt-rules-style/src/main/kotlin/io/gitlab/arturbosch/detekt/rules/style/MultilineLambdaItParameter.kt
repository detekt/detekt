package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import io.gitlab.arturbosch.detekt.rules.hasImplicitParameterReference
import io.gitlab.arturbosch.detekt.rules.implicitParameter
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.resolve.BindingContext

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
@RequiresTypeResolution
class MultilineLambdaItParameter(val config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Multiline lambdas should not use `it` as a parameter name.",
        Debt.FIVE_MINS
    )

    override fun visitCondition(root: KtFile) = bindingContext != BindingContext.EMPTY && super.visitCondition(root)

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val size = lambdaExpression.bodyExpression?.statements?.size
        if (size == null || size <= 1) return

        val parameterNames = lambdaExpression.valueParameters.map { it.name }
        // Explicit `it`
        if (IT_LITERAL in parameterNames) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(lambdaExpression),
                    "The parameter name in a multiline lambda should not be an explicit `it`. " +
                        "Consider giving your parameter a readable and descriptive name."
                )
            )
        } else if (parameterNames.isEmpty()) { // Implicit `it`
            val implicitParameter = lambdaExpression.implicitParameter(bindingContext)
            if (implicitParameter != null &&
                lambdaExpression.hasImplicitParameterReference(implicitParameter, bindingContext)
            ) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(lambdaExpression),
                        "The implicit `it` should not be used in a multiline lambda. " +
                            "Consider giving your parameter a readable and descriptive name."
                    )
                )
            }
        }
    }
}
