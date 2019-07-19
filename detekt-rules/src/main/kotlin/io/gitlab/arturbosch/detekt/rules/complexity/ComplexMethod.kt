package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.internal.McCabeVisitor
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
 * Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
 * Smaller methods can also be named much clearer which leads to improved readability of the code.
 *
 * @configuration threshold - MCC threshold for a method (default: `10`)
 * @configuration ignoreSingleWhenExpression - Ignores a complex method if it only contains a single when expression.
 * (default: `false`)
 * @configuration ignoreSimpleWhenEntries - Whether to ignore simple (braceless) when entries. (default: `false`)
 *
 * @active since v1.0.0
 */
class ComplexMethod(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_ACCEPTED_METHOD_COMPLEXITY
) : ThresholdRule(config, threshold) {

    override val issue = Issue("ComplexMethod",
            Severity.Maintainability,
            "Prefer splitting up complex methods into smaller, easier to understand methods.",
            Debt.TWENTY_MINS)

    private val ignoreSingleWhenExpression = valueOrDefault(IGNORE_SINGLE_WHEN_EXPRESSION, false)
    private val ignoreSimpleWhenEntries = valueOrDefault(IGNORE_SIMPLE_WHEN_ENTRIES, false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (hasSingleWhenExpression(function.bodyExpression)) {
            return
        }
        val visitor = McCabeVisitor(ignoreSimpleWhenEntries)
        visitor.visitNamedFunction(function)
        val mcc = visitor.mcc
        if (mcc >= threshold) {
            report(ThresholdedCodeSmell(issue,
                    Entity.from(function),
                    Metric("MCC", mcc, threshold),
                    "The function ${function.nameAsSafeName} appears to be too complex."))
        }
    }

    private fun hasSingleWhenExpression(bodyExpression: KtExpression?): Boolean {
        if (ignoreSingleWhenExpression) {
            return when {
                bodyExpression is KtBlockExpression && bodyExpression.statements.size == 1 -> {
                    val statement = bodyExpression.statements.single()
                    statement is KtWhenExpression ||
                            (statement is KtReturnExpression && statement.returnedExpression is KtWhenExpression)
                }
                // the case where function-expression syntax is used: `fun test() = when { ... }`
                bodyExpression is KtWhenExpression -> true
                else -> false
            }
        }
        return false
    }

    companion object {
        const val DEFAULT_ACCEPTED_METHOD_COMPLEXITY = 10
        const val IGNORE_SINGLE_WHEN_EXPRESSION = "ignoreSingleWhenExpression"
        const val IGNORE_SIMPLE_WHEN_ENTRIES = "ignoreSimpleWhenEntries"
    }
}
