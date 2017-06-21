package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.formatting.visitors.ConditionalPathVisitor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtProperty

/**
 * @author Artur Bosch
 */
class OptionalReturnKeyword(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Dept.TEN_MINS)

	private val visitor = ConditionalPathVisitor {
		report(CodeSmell(issue, Entity.from(it)))
		withAutoCorrect {
			it.returnKeyword.delete()
		}
	}

	override fun visitDeclaration(dcl: KtDeclaration) {
		if (dcl is KtProperty) {
			dcl.delegateExpressionOrInitializer?.accept(visitor)
		}
		super.visitDeclaration(dcl)
	}
}