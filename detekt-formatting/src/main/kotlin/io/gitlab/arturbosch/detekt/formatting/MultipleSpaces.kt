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
class MultipleSpaces(config: Config) : TokenRule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Debt.FIVE_MINS)

	override fun visitSpaces(space: PsiWhiteSpace) {
		if (!space.textContains('\n') && space.textLength > 1) {
			report(CodeSmell(issue, Entity.from(space, offset = 1)))
			withAutoCorrect {
				(space as LeafPsiElement).replaceWithText(" ")
			}
		}
	}

}
