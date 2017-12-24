package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * This rule reports all cases where exceptions are thrown from a `finally` block. Throwing exceptions from a `finally`
 * block should be avoided as it can lead to confusion and discarded exceptions.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } finally {
 *         throw IOException()
 *     }
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ThrowingExceptionFromFinally(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("ThrowingExceptionFromFinally", Severity.Defect,
			"Do not throw an exception within a finally statement. This can discard exceptions and is confusing.")

	override fun visitFinallySection(finallySection: KtFinallySection) {
		val throwExpressions = finallySection.finalExpression.collectByType<KtThrowExpression>()
		throwExpressions.forEach {
			report(CodeSmell(issue, Entity.from(it), message = ""))
		}
	}
}
