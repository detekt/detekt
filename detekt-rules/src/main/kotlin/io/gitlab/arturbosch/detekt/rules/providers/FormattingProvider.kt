package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.formatting.ConsecutiveBlankLines
import io.gitlab.arturbosch.detekt.rules.formatting.Indentation
import io.gitlab.arturbosch.detekt.rules.formatting.MultipleSpaces
import io.gitlab.arturbosch.detekt.rules.formatting.SpacingAfterComma
import io.gitlab.arturbosch.detekt.rules.formatting.SpacingAfterKeyword
import io.gitlab.arturbosch.detekt.rules.formatting.SpacingAroundColon
import io.gitlab.arturbosch.detekt.rules.formatting.SpacingAroundCurlyBraces
import io.gitlab.arturbosch.detekt.rules.formatting.SpacingAroundOperator
import io.gitlab.arturbosch.detekt.rules.formatting.TrailingSpaces
import io.gitlab.arturbosch.detekt.rules.formatting.UnusedImports

/**
 * @author Artur Bosch
 */
class FormattingProvider : RuleSetProvider {

	override val ruleSetId: String = "formatting"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				Indentation(config),
				ConsecutiveBlankLines(config),
				MultipleSpaces(config),
				SpacingAfterComma(config),
				SpacingAfterKeyword(config),
				SpacingAroundColon(config),
				SpacingAroundCurlyBraces(config),
				SpacingAroundOperator(config),
				TrailingSpaces(config),
				UnusedImports(config)
		))
	}
}