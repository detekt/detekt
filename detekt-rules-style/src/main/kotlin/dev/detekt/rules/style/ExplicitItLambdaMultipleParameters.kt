package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.name.Name
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
        if (Name.identifier("it") in parameterNames && parameterNames.size > 1) {
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    "`it` should not be used as name for a lambda parameter.",
                ),
            )
        }
    }
}
