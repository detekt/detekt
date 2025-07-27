package io.gitlab.arturbosch.detekt.rules.exceptions

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType

/**
 * This rule reports `catch` blocks which check for the type of exception via `is` checks or casts.
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
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class InstanceOfCheckForException(config: Config) :
    Rule(
        config,
        "Instead of catching for a general exception type and checking for a specific exception type, " +
            "use multiple catch blocks."
    ),
    RequiresAnalysisApi {

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val catchParameter = catchClause.catchParameter ?: return
        catchClause.catchBody?.forEachDescendantOfType<KtExpression> {
            if (it.isCheckForSubTypeOf(catchParameter)) {
                report(Finding(Entity.from(it), description))
            }
        }
    }

    private fun KtExpression.isCheckForSubTypeOf(catchParameter: KtParameter): Boolean {
        val (left, right) = when (this) {
            is KtIsExpression -> leftHandSide to typeReference
            is KtBinaryExpressionWithTypeRHS -> if (KtPsiUtil.isUnsafeCast(this)) left to right else null
            else -> null
        } ?: return false

        val leftText = (left as? KtNameReferenceExpression)?.text
        return if (leftText == catchParameter.name) {
            analyze(this) {
                val rightType = right?.type
                val catchType = catchParameter.typeReference?.type
                if (rightType != null && catchType != null) rightType.isSubtypeOf(catchType) else true
            }
        } else {
            false
        }
    }
}
