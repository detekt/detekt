package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression

/**
 * This rule reports all exceptions that are caught and then later re-thrown without modification.
 * It ignores caught exceptions that are rethrown if there is work done before that.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw e
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw MyException(e)
 *     }
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         print(e)
 *         throw e
 *     }
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         print(e.message)
 *         throw e
 *     }
 * }
 * </compliant>
 */
class RethrowCaughtException(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("RethrowCaughtException", Severity.CodeSmell,
            "Do not rethrow a caught exception of the same type.",
            Debt.FIVE_MINS)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val exceptionName = catchClause.catchParameter?.name ?: return
        val statements = catchClause.catchBody?.children ?: return
        val throwExpression = statements.firstOrNull() as? KtThrowExpression
        if (throwExpression != null && throwExpression.thrownExpression?.text == exceptionName) {
            report(CodeSmell(issue, Entity.from(throwExpression), issue.description))
        }
        super.visitCatchSection(catchClause)
    }
}
