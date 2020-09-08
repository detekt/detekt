package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedElementOrCallableRef
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * This rule detects the usage of the Pair constructor to create pairs of values.
 *
 * Using <value1> to <value2> is preferred.
 *
 * <noncompliant>
 * val pair = Pair(1, 2)
 * </noncompliant>
 *
 * <compliant>
 * val pair = 1 to 2
 * </compliant>
 *
 * @requiresTypeResolution
 */
class PreferToOverPairSyntax(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue("PreferToOverPairSyntax", Severity.Style,
            "Pair was created using the Pair constructor, using the to syntax is preferred.",
            Debt.FIVE_MINS)

    @Suppress("ReturnCount")
    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        if (bindingContext == BindingContext.EMPTY) return
        val callReference = expression.getQualifiedElementOrCallableRef() as? KtCallExpression ?: return
        val subjectType =
            callReference.getType(bindingContext)?.constructor?.declarationDescriptor as? ClassDescriptor ?: return

        if (subjectType.fqNameOrNull()?.asString() == PAIR_CONSTRUCTOR_REFERENCE_NAME) {
            val arg = callReference.valueArguments.joinToString(" to ") { it.text }

            report(CodeSmell(issue, Entity.from(expression),
                    message = "Pair is created by using the pair constructor. " +
                            "This can replaced by `$arg`"))
        }

        super.visitSimpleNameExpression(expression)
    }

    companion object {
        const val PAIR_CONSTRUCTOR_REFERENCE_NAME = "kotlin.Pair"
    }
}
