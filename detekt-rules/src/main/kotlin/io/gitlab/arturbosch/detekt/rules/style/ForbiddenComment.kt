package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment

class ForbiddenComment(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Flags a forbidden comment. Defaults values are TODO:, FIXME: or STOPSHIP:",
			Debt.TEN_MINS)

	private val values: List<String>
			= valueOrDefault(VALUES, "TODO:,FIXME:,STOPSHIP:")
			.split(",")
			.filter { it.isNotBlank() }

	override fun visitComment(comment: PsiComment) {
		super.visitComment(comment)

		val text = comment.text

		values.forEach {
			if (text.contains(it, ignoreCase = true)) {
				report(CodeSmell(issue, Entity.from(comment)))
			}
		}
	}

	companion object {
		const val VALUES = "values"
	}
}

