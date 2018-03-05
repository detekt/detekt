package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtValueArgumentList

/**
 * Using a spread operator causes a full copy of the array to be created before calling a method.
 * This has a very high performance penalty.
 * Benchmarks showing this performance penalty can be seen here:
 * https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 *
 * <noncompliant>
 * fun foo(strs: Array<String>) {
 *     bar(*strs)
 * }
 *
 * fun bar(vararg strs: String) {
 *     strs.forEach { println(it) }
 * }
 * </noncompliant>
 *
 * @active since v1.0.0
 * @author Ivan Balaksha
 */
class SpreadOperator(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("SpreadOperator", Severity.Performance,
			"Using a spread operator causes a full copy of the array to be created before calling a method " +
					"has a very high performance penalty.",
			Debt.TWENTY_MINS)

	override fun visitValueArgumentList(list: KtValueArgumentList) {
		super.visitValueArgumentList(list)
		list.arguments.filter { it.getSpreadElement() != null }
				.forEach {
					report(CodeSmell(issue, Entity.from(list), issue.description))
				}
	}
}
