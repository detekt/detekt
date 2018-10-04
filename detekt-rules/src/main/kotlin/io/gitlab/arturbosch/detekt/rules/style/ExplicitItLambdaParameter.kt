package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Lambda expressions are one of the core features of the language. They often include very small chunks of
 * code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
 * to make code more concise. It fits most usecases, but when faced larger or nested chunks of code,
 * you might want to add an explicit name for the parameter. Naming it just `it` is meaningless and only
 * clutters the code.
 *
 * <noncompliant>
 * a?.let { it -> it.plus(1) }
 * foo.flatMapObservable { it -> Observable.fromIterable(it) }
 * listOfPairs.map(::second).forEach { it ->
 * 		it.execute()
 * }
 * </noncompliant>
 *
 * <compliant>
 * a?.let { it.plus(1) } // Much better to use implicit it
 * foo.flatMapObservable(Observable::fromIterable) // Here we can have a method reference
 * listOfPairs.map(::second).forEach { apiRequest -> // For multiline blocks better come up with meaningful name
 * 		it.execute()
 * }
 * </compliant>
 *
 * @author mishkun
 */
class ExplicitItLambdaParameter(val config: Config) : Rule(config) {
	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"Declaring single explicit `it` parameter is redundant", Debt.FIVE_MINS)

	override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
		super.visitLambdaExpression(lambdaExpression)

		val isSingleParameterLambda = lambdaExpression.valueParameters.size == 1
		if (!isSingleParameterLambda) return

		val singleParameter = lambdaExpression.valueParameters.first()
		if (singleParameter.name == IT_LITERAL) {
			report(CodeSmell(
					issue, Entity.from(lambdaExpression),
					"explicit `it` parameter declaration can be omitted"
			))
		}
	}

	companion object {
		private const val IT_LITERAL = "it"
	}
}
