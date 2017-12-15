package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
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
						"use multiple catch blocks.")

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchBody?.collectByType<KtIsExpression>()?.forEach {
			report(CodeSmell(issue, Entity.from(it), message = ""))
		}
		catchClause.catchBody?.collectByType<KtBinaryExpressionWithTypeRHS>()?.forEach {
			if (KtPsiUtil.isUnsafeCast(it)) {
				report(CodeSmell(issue, Entity.from(it), message = ""))
			}
		}
	}
}
