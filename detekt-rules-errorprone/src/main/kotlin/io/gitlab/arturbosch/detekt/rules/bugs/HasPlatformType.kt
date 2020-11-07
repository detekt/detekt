package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.isFlexible

/*
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/1.3.50/idea/src/org/jetbrains/kotlin/idea/intentions/SpecifyTypeExplicitlyIntention.kt#L86-L107
 */
/**
 * Platform types must be declared explicitly in public APIs to prevent unexpected errors.
 *
 * <noncompliant>
 * class Person {
 *   fun apiCall() = System.getProperty("propertyName")
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Person {
 *   fun apiCall(): String = System.getProperty("propertyName")
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class HasPlatformType(config: Config) : Rule(config) {

    override val issue = Issue(
        "HasPlatformType",
        Severity.Maintainability,
        "Platform types must be declared explicitly in public APIs.",
        Debt.FIVE_MINS
    )

    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)
        if (bindingContext == BindingContext.EMPTY) return

        if (element is KtCallableDeclaration && element.hasImplicitPlatformType()) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(element),
                    "$element has implicit platform type. Type must be declared explicitly."
                )
            )
        }
    }

    private fun KtCallableDeclaration.hasImplicitPlatformType(): Boolean {
        fun isPlatFormType(): Boolean {
            if (containingClassOrObject?.isLocal == true) return false
            val callable =
                bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this] as? CallableDescriptor ?: return false

            val isPublicApi = callable.visibility.isPublicAPI
            val isReturnTypeFlexible = callable.returnType?.isFlexible()
            return isPublicApi && isReturnTypeFlexible == true
        }

        return when (this) {
            is KtFunction -> !isLocal && !hasDeclaredReturnType() && isPlatFormType()
            is KtProperty -> !isLocal && typeReference == null && isPlatFormType()
            else -> false
        }
    }
}
