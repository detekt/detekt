package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.hasActualModifier
import org.jetbrains.kotlin.psi.psiUtil.hasExpectModifier
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * Reports empty default constructors. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyDefaultConstructor(config: Config) : EmptyRule(config = config) {

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        if (hasSuitableSignature(constructor) &&
                isNotCalled(constructor) &&
                !isExpectedAnnotationClass(constructor)) {
            report(CodeSmell(issue, Entity.from(constructor), "An empty default constructor can be removed."))
        }
    }

    /**
     * Annotations with the 'expect' or 'actual' keyword need the explicit default constructor - #1362
     */
    private fun isExpectedAnnotationClass(constructor: KtPrimaryConstructor): Boolean {
        val parent = constructor.parent
        if (parent is KtClass && parent.isAnnotation()) {
            return parent.hasExpectModifier() || parent.hasActualModifier()
        }
        return false
    }

    private fun hasSuitableSignature(constructor: KtPrimaryConstructor) =
            hasPublicVisibility(constructor.visibilityModifierType()) &&
                    constructor.annotationEntries.isEmpty() &&
                    constructor.valueParameters.isEmpty()

    private fun hasPublicVisibility(visibility: KtModifierKeywordToken?): Boolean {
        return visibility == null || visibility == KtTokens.PUBLIC_KEYWORD
    }

    private fun isNotCalled(constructor: KtPrimaryConstructor): Boolean {
        return constructor.getContainingClassOrObject().secondaryConstructors.none {
            it.getDelegationCall().isCallToThis && it.getDelegationCall().valueArguments.isEmpty()
        }
    }
}
