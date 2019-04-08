package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import io.gitlab.arturbosch.detekt.rules.parentOfType
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * This rule detects usages of `Void` and reports them as forbidden.
 * The Kotlin type `Unit` should be used instead. This type corresponds to the `Void` class in Java
 * and has only one value - the `Unit` object.
 *
 * <noncompliant>
 * runnable: () -> Void
 * var aVoid: Void? = null
 * </noncompliant>
 *
 * <compliant>
 * runnable: () -> Unit
 * Void::class
 * </compliant>
 *
 * @configuration ignoreOverridden - ignores void types in signatures of overridden functions (default: `false`)
 *
 * @author Egor Neliuba
 * @author Markus Schwarz
 */
class ForbiddenVoid(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "`Unit` should be used instead of `Void`.",
        Debt.FIVE_MINS
    )

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        if (expression.isReferencingVoid()) {
            if (ruleSetConfig.valueOrDefault(IGNORE_OVERRIDDEN, false) && expression.isPartOfOverriddenSignature()) {
                return
            }

            report(CodeSmell(issue, Entity.from(expression), message = "'Void' should be replaced with 'Unit'."))
        }

        super.visitSimpleNameExpression(expression)
    }

    private fun KtSimpleNameExpression.isReferencingVoid() =
        getReferencedName() == Void::class.java.simpleName && !isClassLiteral

    private val KtSimpleNameExpression.isClassLiteral: Boolean
        get() = getStrictParentOfType<KtClassLiteralExpression>() != null

    private fun KtSimpleNameExpression.isPartOfOverriddenSignature() =
        (isPartOfReturnTypeOfFunction() || isParameterTypeOfFunction()) &&
                parentOfType<KtNamedFunction>()?.isOverride() == true

    private fun KtSimpleNameExpression.isPartOfReturnTypeOfFunction() =
        parentOfType<KtNamedFunction>()
            ?.typeReference
            ?.collectDescendantsOfType<KtSimpleNameExpression>()
            ?.any { it == this } ?: false

    private fun KtSimpleNameExpression.isParameterTypeOfFunction() =
        parentOfType<KtParameter>() != null

    companion object {
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
    }
}
