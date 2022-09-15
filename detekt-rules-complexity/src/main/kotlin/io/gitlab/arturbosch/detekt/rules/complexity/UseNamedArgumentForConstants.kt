package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

class UseNamedArgumentForConstants(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "This rule reports when a function is called with the arguments with same type without named arguments.",
        Debt.FIVE_MINS
    )

    @Configuration("The max allow un named arguments of same type for constructors and functions")
    private val threshold: Int by config(defaultValue = 2)

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.valueArguments.all { it.isNamed() }) return

        val constantsArgument = expression.valueArguments.filter { it.getArgumentExpression() is KtConstantExpression }

        if (constantsArgument.isEmpty()) {
            return
        }

        val groupedArguments = constantsArgument.groupBy { it.getArgumentExpression()?.node?.elementType }

        if (groupedArguments.any { arguments ->
                arguments.value.size >= threshold && arguments.value.any { argument -> !argument.isNamed() }
            }) {
            report(expression)
        }
    }

    private fun report(expression: KtCallExpression) {
        val calleeExpression = expression.calleeExpression ?: return
        report(
            CodeSmell(
                issue,
                Entity.from(calleeExpression),
                "This function call can be replaced with named arguments."
            )
        )
    }

    companion object {
        const val THRESHOLD = "threshold"
    }
}
