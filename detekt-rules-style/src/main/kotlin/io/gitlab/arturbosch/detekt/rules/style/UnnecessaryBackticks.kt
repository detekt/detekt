package io.gitlab.arturbosch.detekt.rules.style

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtSimpleNameStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.canPlaceAfterSimpleNameEntry
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isIdentifier

/**
 * This rule reports unnecessary backticks.
 *
 * <noncompliant>
 * class `HelloWorld`
 * </noncompliant>
 *
 * <compliant>
 * class HelloWorld
 * </compliant>
 */
class UnnecessaryBackticks(config: Config) : Rule(
    config,
    "Backticks are unnecessary."
) {

    override fun visitKtElement(element: KtElement) {
        element.allChildren
            .filter { it.node.elementType == KtTokens.IDENTIFIER && it.hasUnnecessaryBackticks() }
            .forEach { report(Finding(Entity.from(it), "Backticks are unnecessary.")) }
        super.visitKtElement(element)
    }

    private fun PsiElement.hasUnnecessaryBackticks(): Boolean {
        val unquoted = text.drop(1).dropLast(1)

        return when {
            (!text.startsWith("`") || !text.endsWith("`")) -> false
            (!unquoted.isIdentifier() || unquoted.isKeyword()) -> false
            else -> canPlaceAfterSimpleNameEntry(
                getStrictParentOfType<KtSimpleNameStringTemplateEntry>()?.nextSibling
            )
        }
    }

    private fun String.isKeyword() = this in KEYWORDS || this.all { it == '_' }

    companion object {
        private val KEYWORDS = KtTokens.KEYWORDS.types.map { it.toString() }
    }
}
