package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCatchClause

/**
 * This rule reports `catch` blocks for exceptions that have a type that is too generic.
 * It should be preferred to catch specific exceptions to the case that is currently handled. If the scope of the caught
 * exception is too broad it can lead to unintended exceptions being caught.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: Exception) { } // too generic exception caught here
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ... do some I/O
 *     } catch(e: IOException) { }
 * }
 * </compliant>
 *
 * @configuration exceptionNames - exceptions which are too generic and should not be caught
 * (default: - ArrayIndexOutOfBoundsException
 *			 - Error
 *			 - Exception
 *			 - IllegalMonitorStateException
 *			 - NullPointerException
 *			 - IndexOutOfBoundsException
 *			 - RuntimeException
 *			 - Throwable)
 *
 * @active since v1.0.0
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 * @author olivierlemasle
 */
class TooGenericExceptionCaught(config: Config) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Caught exception is too generic. " +
					"Prefer catching specific exceptions to the case that is currently handled.",
			Debt.TWENTY_MINS)

	private val exceptions: Set<String> = valueOrDefault(
			CAUGHT_EXCEPTIONS_PROPERTY, caughtExceptionDefaults).toHashSet()

	override fun visitCatchSection(catchClause: KtCatchClause) {
		catchClause.catchParameter?.let {
			val text = it.typeReference?.text
			if (text != null && text in exceptions)
				report(CodeSmell(issue, Entity.from(it), issue.description))
		}
		super.visitCatchSection(catchClause)
	}

	companion object {
		const val CAUGHT_EXCEPTIONS_PROPERTY = "exceptionNames"
	}
}

val caughtExceptionDefaults = listOf(
		"ArrayIndexOutOfBoundsException",
		"Error",
		"Exception",
		"IllegalMonitorStateException",
		"NullPointerException",
		"IndexOutOfBoundsException",
		"RuntimeException",
		"Throwable"
)
