package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.simplePatternToRegex
import io.gitlab.arturbosch.detekt.rules.parentsOfTypeUntil
import io.gitlab.arturbosch.detekt.rules.yieldStatementsSkippingGuardClauses
import org.jetbrains.kotlin.psi.KtLambdaExpression
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
 */
@ActiveByDefault(since = "1.0.0")
class ReturnCount(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Restrict the number of return statements in methods.",
        Debt.TEN_MINS
    )

    @Configuration("define the maximum number of return statements allowed per function")
    private val max: Int by config(2)

    @Configuration("define a list of function names to be ignored by this check")
    private val excludedFunctions: List<Regex> by config(listOf("equals")) { it.map(String::simplePatternToRegex) }

    @Configuration("if labeled return statements should be ignored")
    private val excludeLabeled: Boolean by config(false)

    @Configuration("if labeled return from a lambda should be ignored (takes precedence over excludeLabeled.)")
    private val excludeReturnFromLambda: Boolean by config(true)

    @Configuration("if true guard clauses at the beginning of a method should be ignored")
    private val excludeGuardClauses: Boolean by config(false)

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

    private fun shouldBeIgnored(function: KtNamedFunction) = function.name in excludedFunctions

    private fun countReturnStatements(function: KtNamedFunction): Int {
        fun KtReturnExpression.isExcluded(): Boolean = when {
            excludeReturnFromLambda && isNamedReturnFromLambda() -> true
            excludeLabeled && labeledExpression != null -> true
            else -> false
        }

        val statements = if (excludeGuardClauses) {
            function.yieldStatementsSkippingGuardClauses<KtReturnExpression>()
        } else {
            function.bodyBlockExpression?.statements?.asSequence().orEmpty()
        }

        return statements.flatMap { it.collectDescendantsOfType<KtReturnExpression>().asSequence() }
            .filterNot { it.isExcluded() }
            .count { it.getParentOfType<KtNamedFunction>(true) == function }
    }

    private fun KtReturnExpression.isNamedReturnFromLambda(): Boolean {
        val label = this.labeledExpression
        if (label != null) {
            return this.parentsOfTypeUntil<KtLambdaExpression, KtNamedFunction>()
                .none()
                .not()
        }
        return false
    }
}

private operator fun Iterable<Regex>.contains(input: String?): Boolean {
    return input != null && any { it.matches(input) }
}
