package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.hasActualModifier
import org.jetbrains.kotlin.psi.psiUtil.hasExpectModifier
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * Reports empty default constructors. Empty blocks of code serve no purpose and should be removed.
 */
@ActiveByDefault(since = "1.0.0")
class EmptyDefaultConstructor(config: Config) : EmptyRule(config = config) {

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (hasSuitableSignature(constructor) &&
            isNotCalled(constructor) &&
            !isExpectedOrActualClass(constructor)
        ) {
            report(Finding(Entity.from(constructor), "An empty default constructor can be removed."))
        }
    }

    /**
     * Classes with the 'expect' or 'actual' keyword need the explicit default constructor - #1362 and #3929
     */
    private fun isExpectedOrActualClass(constructor: KtPrimaryConstructor): Boolean {
        val parent = constructor.parent
        if (parent is KtClass) {
            return parent.hasExpectModifier() || parent.hasActualModifier()
        }
        return false
    }

    private fun hasSuitableSignature(constructor: KtPrimaryConstructor) =
        hasPublicVisibility(constructor.visibilityModifierType()) &&
            constructor.annotationEntries.isEmpty() &&
            constructor.valueParameters.isEmpty()

    private fun hasPublicVisibility(visibility: KtModifierKeywordToken?): Boolean =
        visibility == null || visibility == KtTokens.PUBLIC_KEYWORD

    private fun isNotCalled(constructor: KtPrimaryConstructor): Boolean =
        constructor.getContainingClassOrObject().secondaryConstructors.none {
            it.getDelegationCall().isCallToThis && it.getDelegationCall().valueArguments.isEmpty()
        }
}
