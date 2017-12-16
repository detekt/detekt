package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty

/**
 *
 * <noncompliant>
 * val z = if (true) return x else return y
 * </noncompliant>
 *
 * <compliant>
 * val z = if (true) x else y
 * </compliant>
 *
 * @author Artur Bosch
 */
class OptionalReturnKeyword(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"The last expressions inside conditional expressions are always returned. " +
					"This makes the return keyword unnecessary and it can be safely removed.",
			Debt.TEN_MINS)

	private val visitor = ConditionalPathVisitor {
		report(CodeSmell(issue, Entity.from(it), "The return keyword is unnecessary as the last" +
				" statement inside expressions is always returned."))
	}

	override fun visitDeclaration(dcl: KtDeclaration) {
		if (dcl is KtProperty) {
			dcl.delegateExpressionOrInitializer?.accept(visitor)
		}
		super.visitDeclaration(dcl)
	}
}
