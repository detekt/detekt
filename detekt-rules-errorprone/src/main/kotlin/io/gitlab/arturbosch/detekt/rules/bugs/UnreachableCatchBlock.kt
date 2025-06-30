package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Reports unreachable catch blocks.
 * Catch blocks can be unreachable if the exception has already been caught in the block above.
 *
 * <noncompliant>
 * fun test() {
 *     try {
 *         foo()
 *     } catch (t: Throwable) {
 *         bar()
 *     } catch (e: Exception) {
 *         // Unreachable
 *         baz()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun test() {
 *     try {
 *         foo()
 *     } catch (e: Exception) {
 *         baz()
 *     } catch (t: Throwable) {
 *         bar()
 *     }
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class UnreachableCatchBlock(config: Config) :
    Rule(
        config,
        "Unreachable catch block detected."
    ),
    RequiresAnalysisApi {

    override fun visitCatchSection(catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause)

        val tryExpression = catchClause.getStrictParentOfType<KtTryExpression>() ?: return
        val prevCatchClauses = tryExpression.catchClauses.takeWhile { it != catchClause }
        if (prevCatchClauses.isEmpty()) return

        analyze(catchClause) {
            val catchType = catchType(catchClause) ?: return
            val isSubType = prevCatchClauses.any {
                val prevCatchType = catchType(it)
                prevCatchType != null && catchType.isSubtypeOf(prevCatchType)
            }
            if (isSubType) {
                report(Finding(Entity.from(catchClause), "This catch block is unreachable."))
            }
        }
    }

    private fun KaSession.catchType(catchClause: KtCatchClause): KaType? = catchClause.catchParameter?.returnType
}
