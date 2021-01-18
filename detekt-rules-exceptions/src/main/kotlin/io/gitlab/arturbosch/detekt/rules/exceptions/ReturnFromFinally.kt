package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.isInsideOf
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getTargetFunction
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.types.KotlinType

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
 * @active since v1.16.0
 * @requiresTypeResolution
 */
class ReturnFromFinally(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue("ReturnFromFinally", Severity.Defect,
        "Do not return within a finally statement. This can discard exceptions.", Debt.TWENTY_MINS)

    private val ignoreLabeled = valueOrDefault(IGNORE_LABELED, false)

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val finallyBlock = expression.finallyBlock ?: return

        if (expression.isUsedAsExpression(bindingContext) &&
            finallyBlock.typeEqualsTo(expression.getType(bindingContext))
        ) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.Companion.from(finallyBlock),
                    message = "Contents of the finally block do not affect " +
                            "the result of the expression."
                )
            )
        }

        finallyBlock.finalExpression
            .collectDescendantsOfType<KtReturnExpression> { returnExpression ->
                isReturnFromTargetFunction(finallyBlock.finalExpression, returnExpression) &&
                        canFilterLabeledExpression(returnExpression)
            }
            .forEach { report(CodeSmell(issue, Entity.from(it), issue.description)) }
    }

    private fun isReturnFromTargetFunction(
        blockExpression: KtBlockExpression,
        returnStmts: KtReturnExpression
    ): Boolean {
        val targetFunction = returnStmts.getTargetFunction(bindingContext)
            ?: return false

        val targetFunctionBodyExpressionStatements = targetFunction
            .blockExpressionsOrSingle()
            .asIterable()

        return blockExpression.isInsideOf(targetFunctionBodyExpressionStatements)
    }

    private fun canFilterLabeledExpression(
        returnStmt: KtReturnExpression
    ): Boolean = !ignoreLabeled || returnStmt.labeledExpression == null

    private fun KtFinallySection.typeEqualsTo(type: KotlinType?): Boolean {
        val finallyExpression = finalExpression
        if (finallyExpression.statements.isEmpty()) return false

        return finalExpression.getType(bindingContext) == type
    }

    companion object {
        const val IGNORE_LABELED = "ignoreLabeled"
    }
}
