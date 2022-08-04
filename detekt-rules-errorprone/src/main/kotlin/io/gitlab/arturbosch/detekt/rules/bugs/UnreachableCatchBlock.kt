package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.safeAs
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf

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
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class UnreachableCatchBlock(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Unreachable catch block detected.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCatchSection(catchClause: KtCatchClause) {
        super.visitCatchSection(catchClause)

        val tryExpression = catchClause.getStrictParentOfType<KtTryExpression>() ?: return
        val prevCatchClauses = tryExpression.catchClauses.takeWhile { it != catchClause }
        if (prevCatchClauses.isEmpty()) return
        val catchClassDescriptor = catchClause.catchClassDescriptor() ?: return
        if (prevCatchClauses.any { catchClassDescriptor.isSubclassOf(it) }) {
            report(CodeSmell(issue, Entity.from(catchClause), "This catch block is unreachable."))
        }
    }

    private fun KtCatchClause.catchClassDescriptor(): ClassDescriptor? {
        val typeReference = catchParameter?.typeReference ?: return null
        return bindingContext[BindingContext.TYPE, typeReference]?.constructor?.declarationDescriptor?.safeAs()
    }

    private fun ClassDescriptor.isSubclassOf(catchClause: KtCatchClause): Boolean {
        val catchClassDescriptor = catchClause.catchClassDescriptor() ?: return false
        return isSubclassOf(catchClassDescriptor)
    }
}
