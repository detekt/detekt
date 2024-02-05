package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.yieldStatementsSkippingGuardClauses
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Functions should have clear `throw` statements. Functions with many `throw` statements can be harder to read and lead
 * to confusion. Instead, prefer limiting the number of `throw` statements in a function.
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
 */
@ActiveByDefault(since = "1.0.0")
class ThrowsCount(config: Config) : Rule(
    config,
    "Restrict the number of throw statements in methods."
) {

    @Configuration("maximum amount of throw statements in a method")
    private val max: Int by config(2)

    @Configuration("if set to true, guard clauses do not count towards the allowed throws count")
    private val excludeGuardClauses: Boolean by config(false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val statements = if (excludeGuardClauses) {
            function.yieldStatementsSkippingGuardClauses<KtThrowExpression>()
        } else {
            function.bodyBlockExpression?.statements?.asSequence().orEmpty()
        }

        val countOfThrows = statements
            .flatMap { statement ->
                statement.collectDescendantsOfType<KtThrowExpression> {
                    it.getStrictParentOfType<KtNamedFunction>() == function
                }.asSequence()
            }
            .count()

        if (countOfThrows > max) {
            report(
                CodeSmell(
                    Entity.atName(function),
                    "Too many throw statements in the function" +
                        " ${function.nameAsSafeName}. The maximum number of allowed throw statements is $max."
                )
            )
        }
    }
}
