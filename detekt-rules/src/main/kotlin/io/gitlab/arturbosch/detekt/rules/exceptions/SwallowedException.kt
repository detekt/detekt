package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.ALLOWED_EXCEPTION_NAME
import io.gitlab.arturbosch.detekt.rules.isAllowedExceptionName
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

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
 * @configuration allowedExceptionNameRegex - ignores too generic exception types which match this regex
 * (default: `"^(_|(ignore|expected).*)"`)
 */
class SwallowedException(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("SwallowedException", Severity.CodeSmell,
        "The caught exception is swallowed. The original exception could be lost.",
        Debt.TWENTY_MINS)

    private val ignoredExceptionTypes = SplitPattern(valueOrDefault(IGNORED_EXCEPTION_TYPES, ""))

    private val allowedExceptionNameRegex by LazyRegex(ALLOWED_EXCEPTION_NAME_REGEX, ALLOWED_EXCEPTION_NAME)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val exceptionType = catchClause.catchParameter?.typeReference?.text
        if (!ignoredExceptionTypes.contains(exceptionType) &&
            isExceptionSwallowedOrUnused(catchClause) &&
            !catchClause.isAllowedExceptionName(allowedExceptionNameRegex)) {
            report(CodeSmell(issue, Entity.from(catchClause), issue.description))
        }
    }

    private fun isExceptionSwallowedOrUnused(catchClause: KtCatchClause) =
        isExceptionUnused(catchClause) || isExceptionSwallowed(catchClause)

    private fun isExceptionUnused(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        val catchBody = catchClause.catchBody ?: return true
        return !catchBody
            .collectDescendantsOfType<KtNameReferenceExpression>()
            .any { it.text == parameterName }
    }

    private fun isExceptionSwallowed(catchClause: KtCatchClause): Boolean {
        val parameterName = catchClause.catchParameter?.name
        catchClause.catchBody
            ?.collectDescendantsOfType<KtThrowExpression>()
            ?.forEach { throwExpr ->
                val parameterNameReferences = throwExpr.thrownExpression
                    ?.collectDescendantsOfType<KtNameReferenceExpression>()?.filter { it.text == parameterName }
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
        const val ALLOWED_EXCEPTION_NAME_REGEX = "allowedExceptionNameRegex"
    }
}
