package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.SplitPattern
import io.gitlab.arturbosch.detekt.rules.parentsOfTypeUntil
import io.gitlab.arturbosch.detekt.rules.yieldStatementsSkippingGuardClauses
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Restrict the number of return methods allowed in methods.
 *
 * Having many exit points in a function can be confusing and impacts readability of the
 * code.
 *
 * <noncompliant>
 * fun foo(i: Int): String {
 *     when (i) {
 *         1 -> return "one"
 *         2 -> return "two"
 *         else -> return "other"
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(i: Int): String {
 *     return when (i) {
 *         1 -> "one"
 *         2 -> "two"
 *         else -> "other"
 *     }
 * }
 * </compliant>
 *
 * @configuration max - define the maximum number of return statements allowed per function
 * (default: `2`)
 * @configuration excludedFunctions - define functions to be ignored by this check
 * (default: `'equals'`)
 * @configuration excludeLabeled - if labeled return statements should be ignored
 * (default: `false`)
 * @configuration excludeReturnFromLambda - if labeled return from a lambda should be ignored
 * (default: `true`)
 * @configuration excludeGuardClauses - if true guard clauses at the beginning of a method should be ignored
 * (default: `false`)
 * @active since v1.0.0
 */
class ReturnCount(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Restrict the number of return statements in methods.",
        Debt.TEN_MINS
    )

    private val max = valueOrDefault(MAX, 2)
    private val excludedFunctions = SplitPattern(valueOrDefault(EXCLUDED_FUNCTIONS, ""))
    private val excludeLabeled = valueOrDefault(EXCLUDE_LABELED, false)
    private val excludeLambdas = valueOrDefault(EXCLUDE_RETURN_FROM_LAMBDA, true)
    private val excludeGuardClauses = valueOrDefault(EXCLUDE_GUARD_CLAUSES, false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!shouldBeIgnored(function)) {
            val numberOfReturns = countReturnStatements(function)

            if (numberOfReturns > max) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(function),
                        "Function ${function.name} has $numberOfReturns return statements " +
                            "which exceeds the limit of $max."
                    )
                )
            }
        }
    }

    private fun shouldBeIgnored(function: KtNamedFunction) = excludedFunctions.contains(function.name)

    private fun countReturnStatements(function: KtNamedFunction): Int {
        fun KtReturnExpression.isExcluded(): Boolean = when {
            excludeLabeled && labeledExpression != null -> true
            excludeLambdas && isNamedReturnFromLambda() -> true
            else -> false
        }

        val statements = if (excludeGuardClauses) {
            function.yieldStatementsSkippingGuardClauses<KtReturnExpression>()
        } else {
            function.bodyBlockExpression?.statements?.asSequence() ?: emptySequence()
        }

        return statements.flatMap { it.collectDescendantsOfType<KtReturnExpression>().asSequence() }
            .filterNot { it.isExcluded() }
            .filter { it.getParentOfType<KtNamedFunction>(true) == function }
            .count()
    }

    private fun KtReturnExpression.isNamedReturnFromLambda(): Boolean {
        val label = this.labeledExpression
        if (label != null) {
            return this.parentsOfTypeUntil<KtCallExpression, KtNamedFunction>()
                .map { it.calleeExpression }
                .filterIsInstance<KtNameReferenceExpression>()
                .map { it.text }
                .any { it in label.text }
        }
        return false
    }

    companion object {
        const val MAX = "max"
        const val EXCLUDED_FUNCTIONS = "excludedFunctions"
        const val EXCLUDE_LABELED = "excludeLabeled"
        const val EXCLUDE_RETURN_FROM_LAMBDA = "excludeReturnFromLambda"
        const val EXCLUDE_GUARD_CLAUSES = "excludeGuardClauses"
    }
}
