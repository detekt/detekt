package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.collectByType
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * Exceptions should not be swallowed. This rule reports all instances where exceptions are `caught` and not correctly
 * passed into a newly thrown exception.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e.message) // e is swallowed
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException() // e is swallowed
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         bar() // exception is unused
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         throw MyException(e)
 *     }
 *     try {
 *         // ...
 *     } catch(e: IOException) {
 *         println(e) // logging is ok here
 *     }
 * }
 * </compliant>
 *
 * @configuration ignoredExceptionTypes - exception types which should be ignored by this rule
 * (default: `'InterruptedException,NumberFormatException,ParseException,MalformedURLException'`)
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class SwallowedException(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("SwallowedException", Severity.CodeSmell,
        "The caught exception is swallowed. The original exception could be lost.",
        Debt.TWENTY_MINS)

    private val ignoredExceptionTypes = SplitPattern(valueOrDefault(IGNORED_EXCEPTION_TYPES, ""))

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val exceptionType = catchClause.catchParameter?.typeReference?.text
        if (!ignoredExceptionTypes.contains(exceptionType) &&
            isExceptionUnused(catchClause) ||
            isExceptionSwallowed(catchClause)) {
            report(CodeSmell(issue, Entity.from(catchClause), issue.description))
        }
    }

    private fun isExceptionUnused(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        val catchBody = catchClause.catchBody ?: return true
        return !catchBody
            .collectByType<KtNameReferenceExpression>()
            .any { it.text == parameterName }
    }

    private fun isExceptionSwallowed(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        catchClause.catchBody
            ?.collectByType<KtThrowExpression>()
            ?.forEach { throwExpr ->
                val parameterNameReferences = throwExpr.thrownExpression
                    ?.collectByType<KtNameReferenceExpression>()?.filter { it.text == parameterName }
                    ?.toList()
                return hasParameterReferences(parameterNameReferences)
            }
        return false
    }

    private fun hasParameterReferences(parameterNameReferences: List<KtNameReferenceExpression>?): Boolean {
        return parameterNameReferences != null &&
            parameterNameReferences.isNotEmpty() &&
            parameterNameReferences.all { callsMemberOfCaughtException(it) }
    }

    private fun callsMemberOfCaughtException(expression: KtNameReferenceExpression): Boolean {
        return expression.nextSibling?.text == "."
    }

    companion object {
        const val IGNORED_EXCEPTION_TYPES = "ignoredExceptionTypes"
    }
}
