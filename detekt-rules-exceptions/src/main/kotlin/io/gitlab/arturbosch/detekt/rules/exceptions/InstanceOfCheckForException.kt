package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIsExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.forEachDescendantOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

/**
 * This rule reports `catch` blocks which check for the type of an exception via `is` checks or casts.
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
 *
 * </compliant>
 */
class InstanceOfCheckForException(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("InstanceOfCheckForException", Severity.CodeSmell,
            "Instead of checking for a general exception type and checking for a specific exception type " +
                    "use multiple catch blocks.",
            Debt.TWENTY_MINS)

    override fun visitCatchSection(catchClause: KtCatchClause) {
        val catchParameter = catchClause.catchParameter ?: return
        catchClause.catchBody?.forEachDescendantOfType<KtExpression> {
            if (it.isCheckForSubTypeOf(catchParameter)) {
                report(CodeSmell(issue, Entity.from(it), issue.description))
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
            val rightType = bindingContext[BindingContext.TYPE, right]
            val catchType = bindingContext[BindingContext.TYPE, catchParameter.typeReference]
            if (rightType != null && catchType != null) rightType.isSubtypeOf(catchType) else true
        } else {
            false
        }
    }
}
