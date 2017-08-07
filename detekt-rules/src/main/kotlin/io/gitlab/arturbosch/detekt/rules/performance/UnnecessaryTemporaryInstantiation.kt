package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

class UnnecessaryTemporaryInstantiation(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UnnecessaryTemporaryInstantiation", Severity.Performance,
			"Avoid temporary objects when converting primitive types to String")

	private val types: Set<String> = hashSetOf("Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double")

	override fun visitCallExpression(expression: KtCallExpression) {
		if (isPrimitiveWrapperType(expression.calleeExpression)
				&& isToStringMethod(expression.nextSibling?.nextSibling)) {
			report(CodeSmell(issue, Entity.from(expression)))
		}
	}

	private fun isPrimitiveWrapperType(expression: KtExpression?) = types.contains(expression?.text)

	private fun isToStringMethod(element: PsiElement?) = element?.text == "toString()"
}
