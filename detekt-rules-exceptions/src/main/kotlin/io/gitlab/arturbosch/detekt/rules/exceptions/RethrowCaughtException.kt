package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

/**
 * This rule reports all exceptions that are caught and then later re-thrown without modification.
 * It ignores cases:
 * 1. When caught exceptions that are rethrown if there is work done before that.
 * 2. When there are more than one catch in try block and at least one of them has some work.
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
 *
 *     try {
 *         // ...
 *     } catch (e: IOException) {
 *         throw e
 *     } catch (e: Exception) {
 *         print(e.message)
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class RethrowCaughtException(config: Config) : Rule(
    config,
    "Do not rethrow a caught exception of the same type."
) {

    override fun visitTryExpression(tryExpr: KtTryExpression) {
        val catchClauses = tryExpr.getChildrenOfType<KtCatchClause>()
        catchClauses.map { violatingThrowExpressionFrom(it) }
            .takeLastWhile { it != null }
            .forEach { violation ->
                violation?.let {
                    report(CodeSmell(Entity.from(it), description))
                }
            }
        super.visitTryExpression(tryExpr)
    }

    private fun violatingThrowExpressionFrom(catchClause: KtCatchClause): KtThrowExpression? {
        val exceptionName = catchClause.catchParameter?.name
        val throwExpression = catchClause.catchBody?.children?.firstOrNull() as? KtThrowExpression
        if (throwExpression?.thrownExpression?.text == exceptionName) {
            return throwExpression
        }
        return null
    }
}
