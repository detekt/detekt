package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
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
class UnnecessaryBackticks(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Backticks are unnecessary.",
        Debt.FIVE_MINS
    )

    override fun visitKtElement(element: KtElement) {
        element.allChildren
            .filter { it.node.elementType == KtTokens.IDENTIFIER && it.hasUnnecessaryBackticks() }
            .forEach { report(CodeSmell(issue, Entity.from(it), "Backticks are unnecessary.")) }
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
