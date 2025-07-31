package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_KOTLIN_PACKAGE
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Reports unnecessary not-null checks with `requireNotNull` or `checkNotNull` that can be removed by the user.
 *
 * <noncompliant>
 * var string = "foo"
 * println(requireNotNull(string))
 * </noncompliant>
 *
 * <compliant>
 * var string : String? = "foo"
 * println(requireNotNull(string))
 * </compliant>
 */
class UnnecessaryNotNullCheck(config: Config) :
    Rule(
        config,
        "Remove unnecessary not-null checks on non-null types."
    ),
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callee = expression.calleeExpression ?: return
        val argument = expression.valueArguments.firstOrNull()?.getArgumentExpression() ?: return

        analyze(expression) {
            if (expression
                    .resolveToCall()
                    ?.successfulFunctionCallOrNull()
                    ?.symbol
                    ?.callableId !in notNullCheckFunctionFqNames
            ) {
                return
            }
            if (expression.expressionType?.canBeNull == true) return
        }

        analyze(argument) {
            if (argument.expressionType?.canBeNull == true) return
        }

        report(
            Finding(
                entity = Entity.from(expression),
                message = "Using `${callee.text}` on non-null `${argument.text}` is unnecessary",
            )
        )
    }

    companion object {
        private val notNullCheckFunctionFqNames = listOf(
            CallableId(BASE_KOTLIN_PACKAGE, Name.identifier("requireNotNull")),
            CallableId(BASE_KOTLIN_PACKAGE, Name.identifier("checkNotNull")),
        )
    }
}
