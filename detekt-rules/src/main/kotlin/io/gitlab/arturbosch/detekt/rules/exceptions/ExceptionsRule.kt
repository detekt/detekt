package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtParameter

/**
 * @author Artur Bosch
 */
open class ExceptionsRule(id: String, config: Config, severity: Severity = Rule.Severity.Maintainability) : Rule(id, severity, config) {

	inline fun KtCatchClause.addFindingIfExceptionClassMatchesExact(exception: () -> String) {
		matchesClause { text.substringAfter(":").trim() == exception() }
	}

	inline fun KtCatchClause.matchesClause(condition: KtParameter.() -> Boolean) {
		this.catchParameter?.let {
			if (it.condition())
				addFindings(CodeSmell(id, Entity.from(it)))
		}
	}

}