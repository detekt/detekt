package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

/**
 * Exceptions should not be wrapped inside the same exception type and then rethrown. Prefer wrapping exceptions in more
 * meaningful exception types.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IllegalStateException) {
 *         throw IllegalStateException(e) // rethrows the same exception
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IllegalStateException) {
 *         throw MyException(e)
 *     }
 * }
 * </compliant>
 */
class ThrowingNewInstanceOfSameException(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ThrowingNewInstanceOfSameException", Severity.Defect,
            "Avoid catch blocks that rethrow a caught exception wrapped inside a new instance of the same exception.",
            Debt.FIVE_MINS)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val parameterName = catchClause.catchParameter?.name
        val typeReference = catchClause.catchParameter?.typeReference?.text
        val throwExpression = catchClause.catchBody?.findDescendantOfType<KtThrowExpression> {
            val thrownExpression = it.thrownExpression as? KtCallExpression
            thrownExpression != null &&
                    createsSameExceptionType(thrownExpression, typeReference) &&
                    hasSameExceptionParameter(thrownExpression.valueArguments, parameterName)
        }
        if (throwExpression != null) {
            report(CodeSmell(issue, Entity.from(throwExpression), issue.description))
        }
    }

    private fun createsSameExceptionType(thrownExpression: KtCallExpression, typeReference: String?): Boolean {
        return thrownExpression.calleeExpression?.text == typeReference
    }

    private fun hasSameExceptionParameter(valueArguments: List<KtValueArgument>, parameterName: String?): Boolean {
        return valueArguments.size == 1 && valueArguments.first().text == parameterName
    }
}
