package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.yieldStatementsSkippingGuardClauses
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Functions should have clear `throw` statements. Functions with many `throw` statements can be harder to read and lead
 * to confusion. Instead prefer to limit the amount of `throw` statements in a function.
 *
 * <noncompliant>
 * fun foo(i: Int) {
 *     when (i) {
 *         1 -> throw IllegalArgumentException()
 *         2 -> throw IllegalArgumentException()
 *         3 -> throw IllegalArgumentException()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(i: Int) {
 *     when (i) {
 *         1,2,3 -> throw IllegalArgumentException()
 *     }
 * }
 * </compliant>
 *
 * @configuration max - maximum amount of throw statements in a method (default: `2`)
 *
 * @active since v1.0.0
 */
class ThrowsCount(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName, Severity.Style,
        "Restrict the number of throw statements in methods.",
        Debt.TEN_MINS
    )

    private val max = valueOrDefault(MAX, 2)
    private val excludeGuardClauses = valueOrDefault(EXCLUDE_GUARD_CLAUSES, false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (!function.isOverride()) {
            val statements = if (excludeGuardClauses) {
                function.yieldStatementsSkippingGuardClauses<KtThrowExpression>()
            } else {
                function.bodyBlockExpression?.statements?.asSequence() ?: emptySequence()
            }

            val countOfThrows = statements
                .flatMap { it.collectDescendantsOfType<KtThrowExpression>().asSequence() }
                .count()

            if (countOfThrows > max) {
                report(
                    CodeSmell(
                        issue,
                        Entity.atName(function),
                        "Too many throw statements in the function" +
                                " ${function.nameAsSafeName}. The maximum number of allowed throw statements is $max."
                    )
                )
            }
        }
    }

    companion object {
        const val MAX = "max"
        const val EXCLUDE_GUARD_CLAUSES = "excludeGuardClauses"
    }
}
