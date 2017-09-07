package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression

class ExceptionRaisedInUnexpectedLocation(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ExceptionRaisedInUnexpectedLocation", Severity.CodeSmell,
			"This method is not expected to throw exceptions. This can cause weird behavior.")

	val methods: List<String>
			= valueOrDefault(METHOD_NAMES, "toString,hashCode,equals,finalize")
			.split(",")
			.filter { it.isNotBlank() }

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (isPotentialMethod(function) && hasThrowExpression(function.bodyExpression)) {
			report(CodeSmell(issue, Entity.from(function)))
		}
	}

	override fun visitClassInitializer(initializer: KtClassInitializer) {
		if (hasThrowExpression(initializer.body)) {
			report(CodeSmell(issue, Entity.from(initializer)))
		}
	}

	private fun isPotentialMethod(function: KtNamedFunction): Boolean {
		return methods.contains(function.name)
	}

	private fun hasThrowExpression(declaration: KtExpression?): Boolean {
		return declaration?.collectByType<KtThrowExpression>()?.any() ?: false
	}

	companion object {
		const val METHOD_NAMES = "methodNames"
	}
}
