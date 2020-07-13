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
 * Lambda expressions are one of the core features of the language. They often include very small chunks of
 * code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
 * to make code more concise. It fits most usecases, but when faced larger or nested chunks of code,
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
class ExplicitItLambdaParameter(val config: Config) : Rule(config) {
    override val issue = Issue(javaClass.simpleName, Severity.Style,
            "Declaring lambda parameters as `it` is redundant.", Debt.FIVE_MINS)

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)
        val parameterNames = lambdaExpression.valueParameters.map { it.name }
        if (IT_LITERAL in parameterNames) {
            report(CodeSmell(
                    issue, Entity.from(lambdaExpression),
                    "This explicit usage of `it` as the lambda parameter name can be omitted."
            ))
        }
    }
}
