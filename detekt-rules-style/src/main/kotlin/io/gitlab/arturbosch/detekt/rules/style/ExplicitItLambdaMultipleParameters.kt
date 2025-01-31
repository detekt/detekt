package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.builtins.StandardNames.IMPLICIT_LAMBDA_PARAMETER_NAME
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Lambda expressions are one of the core features of the language. They often include very small chunks of
 * code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
 * to make code more concise. It fits most use cases, but when faced larger or nested chunks of code,
 * you might want to add an explicit name for the parameter. Naming it just `it` is meaningless and only
 * makes your code misleading, especially when dealing with nested functions.
 *
 * <noncompliant>
 * collection.zipWithNext { it, next -> Pair(it, next) }
 * </noncompliant>
 *
 * <compliant>
 * // Lambdas with multiple parameter should be named clearly, using it for one of them can be confusing
 * collection.zipWithNext { prev, next ->
 *     Pair(prev, next)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class ExplicitItLambdaMultipleParameters(
    config: Config,
) : Rule(
    config,
    "Declaring lambda parameters as `it` is inappropriate.",
) {
    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val parameterNames = lambdaExpression.valueParameters.map { it.nameAsName }
        if (IMPLICIT_LAMBDA_PARAMETER_NAME in parameterNames && parameterNames.size > 1) {
            report(
                CodeSmell(
                    Entity.from(lambdaExpression),
                    "`it` should not be used as name for a lambda parameter.",
                ),
            )
        }
    }
}
