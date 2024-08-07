package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * This rule detects the usage of the Pair constructor to create pairs of values.
 *
 * Using `<value1>` to `<value2>` is preferred.
 *
 * <noncompliant>
 * val pair = Pair(1, 2)
 * </noncompliant>
 *
 * <compliant>
 * val pair = 1 to 2
 * </compliant>
 *
 */
class PreferToOverPairSyntax(config: Config) :
    Rule(
        config,
        "Pair was created using the Pair constructor, using the to syntax is preferred."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.isPairConstructor()) {
            val arg = expression.valueArguments.joinToString(" to ") { it.text }
            report(
                CodeSmell(
                    Entity.from(expression),
                    message = "Pair is created by using the pair constructor. " +
                        "This can replaced by `$arg`."
                )
            )
        }
    }

    private fun KtCallExpression.isPairConstructor(): Boolean {
        val descriptor = getResolvedCall(bindingContext)?.resultingDescriptor
        val fqName = (descriptor as? ClassConstructorDescriptor)?.containingDeclaration?.fqNameOrNull()
        return fqName == PAIR_FQ_NAME
    }

    companion object {
        private val PAIR_FQ_NAME = FqName("kotlin.Pair")
    }
}
