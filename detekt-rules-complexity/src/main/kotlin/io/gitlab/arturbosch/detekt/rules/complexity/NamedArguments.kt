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
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

/**
 * Reports function invocations which have more parameters than a certain threshold and are all not named.
 *
 * <noncompliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(1, 2, 3, 4)
 * </noncompliant>
 *
 * <compliant>
 * fun sum(a: Int, b: Int, c: Int, d: Int) {
 * }
 * sum(a = 1, b = 2, c = 3, d = 4)
 * </compliant>
 */
@RequiresTypeResolution
class NamedArguments(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "NamedArguments",
        Severity.Maintainability,
        "Named arguments are required for function invocation with many parameters.",
        Debt.FIVE_MINS
    )

    @Configuration("number of parameters that triggers this inspection")
    private val threshold: Int by config(defaultValue = 3)

    @Configuration("ignores when argument values are the same as the parameter names")
    private val ignoreArgumentsMatchingNames: Boolean by config(defaultValue = false)

    override fun visitCallExpression(expression: KtCallExpression) {
        if (bindingContext == BindingContext.EMPTY) return
        val valueArguments = expression.valueArguments
        if (valueArguments.size > threshold && expression.canNameArguments()) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        } else {
            super.visitCallExpression(expression)
        }
    }

    @Suppress("ReturnCount")
    private fun KtCallExpression.canNameArguments(): Boolean {
        val resolvedCall = getResolvedCall(bindingContext) ?: return false
        if (!resolvedCall.candidateDescriptor.hasStableParameterNames()) return false

        val unnamedArguments = valueArguments.mapNotNull { argument ->
            if (argument.isNamed() || argument is KtLambdaArgument) return@mapNotNull null
            val parameter = resolvedCall.getParameterForArgument(argument) ?: return@mapNotNull null
            if (ignoreArgumentsMatchingNames &&
                parameter.name.asString() == argument.getArgumentExpression()?.text
            ) return@mapNotNull null
            argument to parameter
        }
        if (unnamedArguments.isEmpty()) return false

        return unnamedArguments.all { (argument, parameter) ->
            argument.getSpreadElement() != null || parameter.varargElementType == null
        }
    }
}
