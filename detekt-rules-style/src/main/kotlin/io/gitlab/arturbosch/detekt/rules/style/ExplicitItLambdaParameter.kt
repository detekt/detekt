package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.IT_LITERAL
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
 * collection.zipWithNext { it, next -> Pair(it, next) }
 * </noncompliant>
 *
 * <compliant>
 * a?.let { it.plus(1) } // Much better to use implicit it
 * foo.flatMapObservable(Observable::fromIterable) // Here we can have a method reference
 *
 * // For multiline blocks it is usually better come up with a clear and more meaningful name
 * listOfPairs.map(::second).forEach { apiRequest ->
 *     apiRequest.execute()
 * }
 *
 * // Lambdas with multiple parameter should be named clearly, using it for one of them can be confusing
 * collection.zipWithNext { prev, next ->
 *     Pair(prev, next)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class ExplicitItLambdaParameter(config: Config) : Rule(
    config,
    "Declaring lambda parameters as `it` is redundant."
) {

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        if (lambdaExpression.getStrictParentOfType<KtCallableReferenceExpression>() != null) return
        val parameterNames = lambdaExpression.valueParameters.map { it.name }
        if (IT_LITERAL in parameterNames) {
            val message = if (parameterNames.size == 1) {
                "This explicit usage of `it` as the lambda parameter name can be omitted."
            } else {
                "`it` should not be used as name for a lambda parameter."
            }
            report(
                CodeSmell(
                    issue,
                    Entity.from(lambdaExpression),
                    message
                )
            )
        }
    }
}
