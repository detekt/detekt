package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
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
 * a?.let { it -> it.plus(1) }
 * foo.flatMapObservable { it -> Observable.fromIterable(it) }
 * listOfPairs.map(::second).forEach { it ->
 *     it.execute()
 * }
 * </noncompliant>
 *
 * <compliant>
 * a?.let { it.plus(1) } // Much better to use implicit it
 * a?.let { value: Int -> value.plus(1) } // Better as states the type more clearly
 * foo.flatMapObservable(Observable::fromIterable) // Here we can have a method reference
 *
 * // For multiline blocks it is usually better come up with a clear and more meaningful name
 * listOfPairs.map(::second).forEach { apiRequest ->
 *     apiRequest.execute()
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class ExplicitItLambdaParameter(
    config: Config,
) : Rule(
    config,
    "Declaring lambda parameters as `it` is redundant.",
) {
    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val parameterNames = lambdaExpression.valueParameters.map { it.nameAsName }
        if (IMPLICIT_LAMBDA_PARAMETER_NAME in parameterNames && parameterNames.size == 1) {
            val message =
                if (lambdaExpression.valueParameters[0].typeReference == null) {
                    "This explicit usage of `it` as the lambda parameter name can be omitted."
                } else {
                    "`it` should not be used as name for a lambda parameter."
                }
            report(
                Finding(
                    Entity.from(lambdaExpression),
                    message,
                ),
            )
        }
    }
}
