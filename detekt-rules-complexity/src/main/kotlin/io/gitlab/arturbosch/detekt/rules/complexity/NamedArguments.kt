package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Reports functions invocations which have more parameters than a certain threshold and are not named.
 *
 * @configuration threshold - number of non-named parameters allowed in function invocation (default: `3`)
 */
class NamedArguments(
    config: Config = Config.empty,
    threshold: Int = DEFAULT_FUNCTION_THRESHOLD
) : ThresholdRule(config, threshold) {

    override val issue = Issue(
        "NamedArguments", Severity.Maintainability,
        "Function invocation with more number of parameters must be named.",
        Debt.FIVE_MINS
    )

    private val functionInvocationThreshold: Int =
        valueOrDefault(
            THRESHOLD, valueOrDefault(THRESHOLD, DEFAULT_FUNCTION_THRESHOLD)
        )

    override fun visitCallExpression(expression: KtCallExpression) {
        val valueArguments = expression.valueArguments
        if (valueArguments.size > functionInvocationThreshold && valueArguments.any { !it.isNamed() }) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        } else {
            super.visitCallExpression(expression)
        }
    }

    companion object {
        const val THRESHOLD = "threshold"
        const val DEFAULT_FUNCTION_THRESHOLD = 3
    }
}
