package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Dept
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.TokenRule
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf

/**
 * @author Artur Bosch
 */
class TrailingSpaces(config: Config) : TokenRule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "", Dept.FIVE_MINS)

	override fun visitSpaces(space: PsiWhiteSpace) {
		val candidates = space.text.split("\n")
		if (candidates.size > 1) {
			replaceTracingSpaces(space, candidates.dropLast(),
					replaceWith = "\n".repeat(candidates.size - 1) + candidates.last())
		} else if (space.nextIsEOF()) {
			replaceTracingSpaces(space, candidates,
					replaceWith = "\n".repeat(candidates.size - 1))
		}
	}

	private fun PsiElement.nextIsEOF() = nextLeaf() == null

	private fun replaceTracingSpaces(node: PsiWhiteSpace, candidates: List<String>, replaceWith: String) {
		if (candidates.find { !it.isEmpty() } != null) {
			report(CodeSmell(issue, Entity.from(node)))
			withAutoCorrect {
				(node as LeafPsiElement).replaceWithText(replaceWith)
			}
		}
	}

}