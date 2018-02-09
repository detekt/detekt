package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiComment

/**
 * This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
 * development. Offending code comments will then be reported.
 *
 * <noncompliant>
 * // TODO:,FIXME:,STOPSHIP:
 * fun foo() { }
 * </noncompliant>
 *
 * @configuration values - forbidden comment strings (default: 'TODO:,FIXME:,STOPSHIP:')
 *
 * @active since v1.0.0
 * @author Niklas Baudy
 * @author Marvin Ramin
 */
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
				report(CodeSmell(issue, Entity.from(comment), "This comment contains text that has been " +
						"defined as forbidden in detekt."))
			}
		}
	}

	companion object {
		const val VALUES = "values"
	}
}

