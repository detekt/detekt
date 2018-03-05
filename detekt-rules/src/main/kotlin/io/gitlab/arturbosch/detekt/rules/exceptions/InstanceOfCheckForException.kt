package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

/**
 * This rule reports `catch` blocks which check for the type of an exception via `is` checks or casts.
 * Instead of catching generic exception types and then checking for specific exception types the code should
 * use multiple catch blocks. These catch blocks should then catch the specific exceptions.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: IOException) {
 *         if (e is MyException || (e as MyException) != null) { }
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: MyException) {
 *     } catch(e: IOException) {
 *     }
 *
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class InstanceOfCheckForException(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("InstanceOfCheckForException", Severity.CodeSmell,
			"Instead of checking for a general exception type and checking for a specific exception type " +
						"use multiple catch blocks.",
			Debt.TWENTY_MINS)

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchBody?.collectByType<KtIsExpression>()?.forEach {
			report(CodeSmell(issue, Entity.from(it), issue.description))
		}
		catchClause.catchBody?.collectByType<KtBinaryExpressionWithTypeRHS>()?.forEach {
			if (KtPsiUtil.isUnsafeCast(it)) {
				report(CodeSmell(issue, Entity.from(it), issue.description))
			}
		}
	}
}
