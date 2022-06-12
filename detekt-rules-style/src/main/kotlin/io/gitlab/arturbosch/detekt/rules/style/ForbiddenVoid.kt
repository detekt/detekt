package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.getAbbreviatedTypeOrType

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
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class ForbiddenVoid(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "`Unit` should be used instead of `Void`.",
        Debt.FIVE_MINS
    )

    @Configuration("ignores void types in signatures of overridden functions")
    private val ignoreOverridden: Boolean by config(false)

    @Configuration("ignore void types as generic arguments")
    private val ignoreUsageInGenerics: Boolean by config(false)

    @Suppress("ReturnCount")
    override fun visitTypeReference(typeReference: KtTypeReference) {
        if (bindingContext == BindingContext.EMPTY) return
        val kotlinType = typeReference.getAbbreviatedTypeOrType(bindingContext) ?: return

        if (kotlinType.fqNameOrNull() == VOID_FQ_NAME) {
            if (ignoreOverridden && typeReference.isPartOfOverriddenSignature()) {
                return
            }
            if (ignoreUsageInGenerics && typeReference.isGenericArgument()) {
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
        private val VOID_FQ_NAME = FqName("java.lang.Void")
    }
}
