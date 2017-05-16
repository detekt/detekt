package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class SingleExpressionEqualsOnSameLine(config: Config = Config.empty) : Rule(
		"SingleExpressionEqualsOnSameLine", Severity.Style, config) {

	override fun visitNamedFunction(function: KtNamedFunction) {
		function.equalsToken?.let { equals ->
			function.bodyExpression?.let {
				val equalsLine = Location.startLineAndColumn(equals).line
				val exprLine = Location.startLineAndColumn(it).line
				if (equalsLine != exprLine) {
					addFindings(CodeSmell(id, Entity.from(equals)))
				}
			}
		}
		println(function.bodyExpression?.javaClass)
	}
}