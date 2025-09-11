package dev.detekt.rules.style

import com.intellij.psi.PsiWhiteSpace
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isPartOf
import dev.detekt.psi.isPartOfString
import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression

/**
 * This rule reports if tabs are used in Kotlin files.
 * According to
 * [Google's Kotlin style guide](https://android.github.io/kotlin-guides/style.html#whitespace-characters)
 * the only whitespace chars that are allowed in a source file are the line terminator sequence
 * and the ASCII horizontal space character (0x20). Strings containing tabs are allowed.
 */
class NoTabs(config: Config) : Rule(
    config,
    "Checks if tabs are used in Kotlin files."
) {

    override fun visitWhiteSpace(space: PsiWhiteSpace) {
        super.visitWhiteSpace(space)
        if (space.isTab()) {
            report(Finding(Entity.from(space), "Tab character is in use."))
        }
    }

    private fun PsiWhiteSpace.isTab(): Boolean = (!isPartOfString() || isStringInterpolated()) && text.contains('\t')

    private fun PsiWhiteSpace.isStringInterpolated(): Boolean =
        this.isPartOf<KtStringTemplateEntryWithExpression>()
}
