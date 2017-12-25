package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Flags duplicate case statements in when expressions.
 *
 * If a when expression contains the same case statement multiple times they should be merged. Otherwise it might be
 * easy to miss one of the cases when reading the code, leading to unwanted side effects.
 *
 * <noncompliant>
 * when (i) {
 *     1 -> println("one")
 *     1 -> println("one")
 *     else -> println("else")
 * }
 * </noncompliant>
 *
 * <compliant>
 * when (i) {
 *     1 -> println("one")
 *     else -> println("else")
 * }
 * </compliant>
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 */
class DuplicateCaseInWhenExpression(config: Config) : Rule(config) {

	override val issue = Issue("DuplicateCaseInWhenExpression",
			Severity.Warning,
			"Duplicated case statements in when expression. " +
					"Both cases should be merged.",
			Debt.TEN_MINS)

	override fun visitWhenExpression(expression: KtWhenExpression) {
		val entries = expression.entries
				.map { it.conditions }
				.fold(mutableListOf<String>(), { state, conditions ->
					state.apply { add(conditions.joinToString { it.text }) }
				})
		val duplicates = findDuplicates(entries)
		if (duplicates.isNotEmpty()) {
			report(CodeSmell(issue, Entity.from(expression),
					"When expression has multiple case statements " + "for ${duplicates.joinToString { ", " }}."))
		}
	}

	private fun findDuplicates(list: List<String>): MutableSet<String> {
		val duplicates = mutableSetOf<String>()
		for (i in 0 until list.size) {
			for (j in i+1 until list.size) {
				if (list[i] == list[j]) {
					duplicates.add(list[i])
				}
			}
		}
		return duplicates
	}
}
