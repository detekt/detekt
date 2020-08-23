package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getAbbreviatedTypeOrType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

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
 * @configuration ignoreUsageInGenerics - ignore void types as generic arguments (default: `false`)
 *
 * @requiresTypeResolution
 */
class ForbiddenVoid(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "`Unit` should be used instead of `Void`.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitTypeReference(typeReference: KtTypeReference) {
        if (bindingContext == BindingContext.EMPTY) return
        val kotlinType = typeReference.getAbbreviatedTypeOrType(bindingContext) ?: return

        if (kotlinType.constructor.declarationDescriptor?.fqNameOrNull()?.asString() == VOID_CLASS_NAME) {
            if (ruleSetConfig.valueOrDefault(IGNORE_OVERRIDDEN, false) && typeReference.isPartOfOverriddenSignature()) {
                return
            }
            if (ruleSetConfig.valueOrDefault(IGNORE_USAGE_IN_GENERICS, false) && typeReference.isGenericArgument()) {
                return
            }
            report(CodeSmell(issue, Entity.from(typeReference), message = "'Void' should be replaced with 'Unit'."))
        }

        super.visitTypeReference(typeReference)
    }

    private fun KtTypeReference.isPartOfOverriddenSignature() =
        (isPartOfReturnTypeOfFunction() || isParameterTypeOfFunction()) &&
                getStrictParentOfType<KtNamedFunction>()?.isOverride() == true

    private fun KtTypeReference.isPartOfReturnTypeOfFunction() =
        getStrictParentOfType<KtNamedFunction>()
            ?.typeReference
            ?.anyDescendantOfType<KtTypeReference> { it == this } ?: false

    private fun KtTypeReference.isParameterTypeOfFunction() =
        getStrictParentOfType<KtParameter>() != null

    private fun KtTypeReference.isGenericArgument() =
        getStrictParentOfType<KtTypeArgumentList>() != null

    companion object {
        const val IGNORE_OVERRIDDEN = "ignoreOverridden"
        const val IGNORE_USAGE_IN_GENERICS = "ignoreUsageInGenerics"
        const val VOID_CLASS_NAME = "java.lang.Void"
    }
}
