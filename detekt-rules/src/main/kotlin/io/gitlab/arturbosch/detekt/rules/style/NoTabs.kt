package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPartOf
import io.gitlab.arturbosch.detekt.rules.isPartOfString
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression

/**
 * This rule reports if tabs are used in Kotlin files.
 * According to
 * [Google's Kotlin style guide](https://android.github.io/kotlin-guides/style.html#whitespace-characters)
 * the only whitespace chars that are allowed in a source file are the line terminator sequence
 * and the ASCII horizontal space character (0x20). Strings containing tabs are allowed.
 */
class NoTabs(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Style,
            "Checks if tabs are used in Kotlin files.",
            Debt.FIVE_MINS)

    fun findTabs(file: KtFile) {
        file.collectWhitespaces()
                .filter { it.isTab() }
                .forEach { report(CodeSmell(issue, Entity.from(it), "Tab character is in use.")) }
    }

    private fun KtFile.collectWhitespaces(): List<PsiWhiteSpace> {
        val list = mutableListOf<PsiWhiteSpace>()
        this.accept(object : DetektVisitor() {
            override fun visitWhiteSpace(space: PsiWhiteSpace?) {
                if (space != null) {
                    list.add(space)
                }
                super.visitWhiteSpace(space)
            }
        })
        return list
    }

    private fun PsiWhiteSpace.isTab(): Boolean {
        return (!isPartOfString() || isStringInterpolated()) && text.contains('\t')
    }

    private fun PsiWhiteSpace.isStringInterpolated(): Boolean =
            this.isPartOf<KtStringTemplateEntryWithExpression>()
}
