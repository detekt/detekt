package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.simplePatternToRegex
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

/**
 * Restrict the number of returns allowed in methods.
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
class ReturnCount(config: Config) : Rule(config, "Restrict the number of return statements in methods.") {

    @Configuration("define the maximum number of return statements allowed per function")
    private val max: Int by config(2)

    @Configuration("define a list of function names to be ignored by this check")
    private val excludedFunctions: List<Regex> by config(listOf("equals")) { it.map(String::simplePatternToRegex) }

    @Configuration("if labeled return statements should be ignored")
    private val excludeLabeled: Boolean by config(false)

    @Configuration("if labeled return from a lambda should be ignored")
    private val excludeReturnFromLambda: Boolean by config(true)

    @Configuration("if true guard clauses at the beginning of a method should be ignored")
    private val excludeGuardClauses: Boolean by config(false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (!shouldBeIgnored(function)) {
            val numberOfReturns = countReturnStatements(function)

            if (numberOfReturns > max) {
                report(
                    Finding(
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
        fun KtReturnExpression.isExcluded(): Boolean =
            (excludeReturnFromLambda && isNamedReturnFromLambda()) ||
                (excludeLabeled && labeledExpression != null)

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
            return this.getParentOfType<KtLambdaExpression>(true, KtNamedFunction::class.java) != null
        }
        return false
    }
}

private operator fun Iterable<Regex>.contains(input: String?): Boolean = input != null && any { it.matches(input) }
