package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
open class ExceptionsRule(id: String, config: Config, severity: Severity = Rule.Severity.Maintainability) : Rule(id, severity, config) {

	fun KtCatchClause.addFindingIfExceptionClassMatchesExact(exception: () -> String) {
		this.catchParameter?.let {
			val text = it.typeReference?.text
			if (text != null && text == exception())
				addFindings(CodeSmell(id, Entity.from(it)))
		}
	}

	fun KtThrowExpression.addFindingIfThrowingClassMatchesExact(exception: () -> String) {
		thrownExpression?.text?.substringBefore("(")?.let {
			if (it == exception()) addFindings(CodeSmell(id, Entity.from(this)))
		}
	}

}