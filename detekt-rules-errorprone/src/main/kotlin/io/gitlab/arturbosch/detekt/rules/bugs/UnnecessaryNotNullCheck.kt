package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.getDataFlowAwareTypes
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.types.isNullable

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
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val compilerResources = compilerResources ?: return

        val callee = expression.calleeExpression ?: return
        val argument = expression.valueArguments.firstOrNull()?.getArgumentExpression() ?: return

        if (!expression.isCalling(notNullCheckFunctionFqNames, bindingContext)) return

        val dataFlowAwareTypes = argument.getDataFlowAwareTypes(
            bindingContext,
            compilerResources.languageVersionSettings,
            compilerResources.dataFlowValueFactory
        )
        if (dataFlowAwareTypes.all { it.isNullable() }) return

        report(
            CodeSmell(
                entity = Entity.from(expression),
                message = "Using `${callee.text}` on non-null `${argument.text}` is unnecessary",
            )
        )
    }

    companion object {
        private val notNullCheckFunctionFqNames = listOf(
            FqName("kotlin.requireNotNull"),
            FqName("kotlin.checkNotNull"),
        )
    }
}
