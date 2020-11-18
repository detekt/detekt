package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Reports all `return` statements in `finally` blocks.
 * Using `return` statements in `finally` blocks can discard and hide exceptions that are thrown in the `try` block.
 * Furthermore, this rule reports values from `finally` blocks, if the corresponding `try` is used as an expression.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         throw MyException()
 *     } finally {
 *         return // prevents MyException from being propagated
 *     }
 * }
 *
 * val a: String = try { "s" } catch (e: Exception) { "e" } finally { "f" }
 * </noncompliant>
 *
 * @configuration ignoreLabeled - ignores labeled return statements (default: `false`)
 * @requiresTypeResolution
 */
class ReturnFromFinally(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ReturnFromFinally", Severity.Defect,
        "Do not return within a finally statement. This can discard exceptions.", Debt.TWENTY_MINS)

    private val ignoreLabeled = valueOrDefault(IGNORE_LABELED, false)

    override fun visitFinallySection(finallySection: KtFinallySection) {
        val innerFunctions = finallySection.finalExpression
            .collectDescendantsOfType<KtNamedFunction>()
        finallySection.finalExpression
            .collectDescendantsOfType<KtReturnExpression> { isNotInInnerFunction(it, innerFunctions) &&
                    canFilterLabeledExpression(it) }
            .forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val finallyBlock = expression.finallyBlock

        if (expression.isUsedAsExpression(bindingContext) && finallyBlock != null) {
            val finallyExpression = finallyBlock.finalExpression
            if (finallyExpression.statements.isEmpty()) return

            val finallyReturnType = finallyBlock.finalExpression.getType(bindingContext)

            if (finallyReturnType == expression.getType(bindingContext)) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.Companion.from(finallyBlock),
                        message = issue.description
                    )
                )
            }
        }
    }

    private fun isNotInInnerFunction(
        returnStmts: KtReturnExpression,
        childFunctions: Collection<KtNamedFunction>
    ): Boolean = !returnStmts.parents.any { childFunctions.contains(it) }

    private fun canFilterLabeledExpression(
        returnStmt: KtReturnExpression
    ): Boolean = !ignoreLabeled || returnStmt.labeledExpression == null

    companion object {
        const val IGNORE_LABELED = "ignoreLabeled"
    }
}
