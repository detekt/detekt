package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/*
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/1.3.50/idea/src/org/jetbrains/kotlin/idea/intentions/SpecifyTypeExplicitlyIntention.kt#L86-L107
 */

/**
 * Platform types must be declared explicitly in public APIs to prevent unexpected errors.
 *
 * <noncompliant>
 * class Person {
 *     fun apiCall() = System.getProperty("propertyName")
 * }
 * </noncompliant>
 *
 * <compliant>
 * class Person {
 *     fun apiCall(): String = System.getProperty("propertyName")
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class HasPlatformType(config: Config) :
    Rule(
        config,
        "Platform types must be declared explicitly in public APIs."
    ),
    RequiresAnalysisApi {

    override fun visitKtElement(element: KtElement) {
        super.visitKtElement(element)

        if (element is KtCallableDeclaration && element.hasImplicitPlatformType()) {
            report(
                Finding(
                    Entity.from(element),
                    "$element has implicit platform type. Type must be declared explicitly."
                )
            )
        }
    }

    private fun KtCallableDeclaration.hasImplicitPlatformType(): Boolean {
        fun isPlatFormType(): Boolean {
            if (containingClassOrObject?.isLocal == true) return false
            return analyze(this) {
                symbol.visibility == KaSymbolVisibility.PUBLIC && returnType is KaFlexibleType
            }
        }

        return when (this) {
            is KtFunction -> !isLocal && !hasDeclaredReturnType() && isPlatFormType()
            is KtProperty -> !isLocal && typeReference == null && isPlatFormType()
            else -> false
        }
    }
}
