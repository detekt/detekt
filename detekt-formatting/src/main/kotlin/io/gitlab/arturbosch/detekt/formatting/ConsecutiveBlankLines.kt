package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * @author Artur Bosch
 */
class ConsecutiveBlankLines(config: Config) : TokenRule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Debt.FIVE_MINS)

	override fun visitSpaces(space: PsiWhiteSpace) {
		val parts = space.text.split("\n")
		if (parts.size > ENOUGH_BLANK_LINES) {
			report(CodeSmell(issue, Entity.from(space, offset = 2)))
			withAutoCorrect {
				(space as LeafPsiElement).replaceWithText("${parts.first()}\n\n${parts.last()}")
			}
		}
	}

}

private const val ENOUGH_BLANK_LINES = 3
