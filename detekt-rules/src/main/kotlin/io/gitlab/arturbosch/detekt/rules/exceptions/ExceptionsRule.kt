package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
open class ExceptionsRule(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Defect, "")

	fun KtCatchClause.addFindingIfExceptionClassMatchesExact(exception: () -> String) {
		this.catchParameter?.let {
			val text = it.typeReference?.text
			if (text != null && text == exception())
				report(CodeSmell(issue, Entity.from(it)))
		}
	}

	fun KtThrowExpression.addFindingIfThrowingClassMatchesExact(exception: () -> String) {
		thrownExpression?.text?.substringBefore("(")?.let {
			if (it == exception()) report(CodeSmell(issue, Entity.from(this)))
		}
	}

}