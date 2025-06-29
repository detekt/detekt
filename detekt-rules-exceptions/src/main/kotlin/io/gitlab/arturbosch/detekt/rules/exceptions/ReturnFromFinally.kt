package io.gitlab.arturbosch.detekt.rules.exceptions

import com.intellij.psi.util.parentOfType
import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtFinallySection
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.isInsideOf

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
 */
@ActiveByDefault(since = "1.16.0")
class ReturnFromFinally(config: Config) :
    Rule(
        config,
        "Do not return within a finally statement. This can discard exceptions."
    ),
    RequiresAnalysisApi {

    @Configuration("ignores labeled return statements")
    private val ignoreLabeled: Boolean by config(false)

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)

        val finallyBlock = expression.finallyBlock ?: return

        analyze(expression) {
            if (expression.isUsedAsExpression && finallyBlock.typeEqualsTo(expression.expressionType)) {
                report(
                    Finding(
                        entity = Entity.Companion.from(finallyBlock),
                        message = "Contents of the finally block do not affect " +
                            "the result of the expression."
                    )
                )
            }
        }

        finallyBlock.finalExpression
            .collectDescendantsOfType<KtReturnExpression> { returnExpression ->
                isReturnFromTargetFunction(finallyBlock.finalExpression, returnExpression) &&
                    canFilterLabeledExpression(returnExpression)
            }
            .forEach { report(Finding(Entity.from(it), description)) }
    }

    private fun isReturnFromTargetFunction(
        blockExpression: KtBlockExpression,
        returnStmts: KtReturnExpression,
    ): Boolean {
        val targetFunction = returnStmts.parentOfType<KtCallableDeclaration>() ?: return false

        val targetFunctionBodyExpressionStatements = targetFunction
            .blockExpressionsOrSingle()
            .asIterable()

        return blockExpression.isInsideOf(targetFunctionBodyExpressionStatements)
    }

    private fun canFilterLabeledExpression(
        returnStmt: KtReturnExpression,
    ): Boolean = !ignoreLabeled || returnStmt.labeledExpression == null

    private fun KtFinallySection.typeEqualsTo(type: KaType?): Boolean {
        val finallyExpression = finalExpression
        if (finallyExpression.statements.isEmpty()) return false

        return analyze(finalExpression) {
            finalExpression.expressionType == type
        }
    }
}
