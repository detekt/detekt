package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * @author Artur Bosch
 */
open class ExceptionsRule(id: String, config: Config) : Rule(id, config) {

	fun KtCatchClause.addFindingIfExceptionClassMatchesExact(context: Context, issue: Issue, exception: () -> String) {
		this.catchParameter?.let {
			val text = it.typeReference?.text
			if (text != null && text == exception())
				context.report(CodeSmell(issue, Entity.from(it)))
		}
	}

	fun KtThrowExpression.addFindingIfThrowingClassMatchesExact(context: Context, issue: Issue, exception: () -> String) {
		thrownExpression?.text?.substringBefore("(")?.let {
			if (it == exception()) context.report(CodeSmell(issue, Entity.from(this)))
		}
	}

}