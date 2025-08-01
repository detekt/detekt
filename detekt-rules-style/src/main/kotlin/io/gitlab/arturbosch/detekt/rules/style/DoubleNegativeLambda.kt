package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.api.valuesWithReason
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * Detects negation in lambda blocks where the function name is also in the negative (like `takeUnless`).
 * A double negative is harder to read than a positive. In particular, if there are multiple conditions with `&&` etc. inside
 * the lambda, then the reader may need to unpack these using DeMorgan's laws. Consider rewriting the lambda to use a positive version
 * of the function (like `takeIf`).
 *
 * <noncompliant>
 * fun Int.evenOrNull() = takeUnless { it % 2 != 0 }
 * </noncompliant>
 * <compliant>
 * fun Int.evenOrNull() = takeIf { it % 2 == 0 }
 * </compliant>
 */
class DoubleNegativeLambda(config: Config) : Rule(
    config,
    "Double negative from a function name expressed in the negative (like `takeUnless`) with a lambda block " +
        "that also contains negation. This is more readable when rewritten using a positive form of the function " +
        "(like `takeIf`).",
) {

    private val splitCamelCaseRegex = "(?<=[a-z])(?=[A-Z])".toRegex()

    private val negationTokens = listOf(
        KtTokens.EXCL,
        KtTokens.EXCLEQ,
        KtTokens.EXCLEQEQEQ,
        KtTokens.NOT_IN,
        KtTokens.NOT_IS,
    )

    @Configuration(
        "Function names expressed in the negative that can form double negatives with their lambda blocks. " +
            "These are grouped together with a recommendation to use a positive counterpart, or `null` if this is " +
            "unknown."
    )
    private val negativeFunctions: List<NegativeFunction> by config(
        valuesWithReason(
            "takeUnless" to "Use `takeIf` instead.",
            "none" to "Use `all` instead."
        )
    ) { list ->
        list.map { NegativeFunction(simpleName = it.value, recommendation = it.reason) }
    }

    @Configuration(
        "Function name parts to look for in the lambda block when deciding " +
            "if the lambda contains a negative."
    )
    private val negativeFunctionNameParts: Set<String> by config(listOf("not", "non")) { it.toSet() }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val calleeExpression = expression.calleeExpression?.text ?: return
        val negativeFunction = negativeFunctions.firstOrNull { it.simpleName == calleeExpression } ?: return
        val lambdaArgument = expression.lambdaArguments.firstOrNull() ?: return

        val forbiddenChildren = lambdaArgument.collectDescendantsOfType<KtExpression> {
            it.isForbiddenNegation()
        }

        if (forbiddenChildren.isNotEmpty()) {
            report(
                Finding(
                    Entity.from(expression),
                    formatMessage(forbiddenChildren, negativeFunction)
                )
            )
        }
    }

    private fun KtExpression.isForbiddenNegation(): Boolean =
        when (this) {
            is KtOperationReferenceExpression -> operationSignTokenType in negationTokens
            is KtCallExpression -> {
                text == "not()" ||
                    text.split(splitCamelCaseRegex).map { it.lowercase() }.any { it in negativeFunctionNameParts }
            }
            else -> false
        }

    private fun formatMessage(
        forbiddenChildren: List<KtExpression>,
        negativeFunction: NegativeFunction,
    ) = buildString {
        append("Double negative through using ${forbiddenChildren.joinInBackTicks()} inside a ")
        append("`${negativeFunction.simpleName}` lambda. ")
        if (negativeFunction.recommendation != null) {
            append(negativeFunction.recommendation)
        } else {
            append("Rewrite in the positive.")
        }
    }

    private fun List<KtExpression>.joinInBackTicks() = joinToString { "`${it.text}`" }

    /**
     * A function that can form a double negative with its lambda.
     */
    private data class NegativeFunction(
        val simpleName: String,
        val recommendation: String?,
    )

    companion object {
        const val NEGATIVE_FUNCTIONS = "negativeFunctions"
        const val NEGATIVE_FUNCTION_NAME_PARTS = "negativeFunctionNameParts"
    }
}
