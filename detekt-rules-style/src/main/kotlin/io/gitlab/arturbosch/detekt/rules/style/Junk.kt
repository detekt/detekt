package io.gitlab.arturbosch.detekt.rules.style

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * Util function to search for the first [KtElement] in the parents of
 * the given [line] from a given offset in a [KtFile].
 */
internal fun findFirstKtElementInParents(file: KtFile, offset: Int, line: String): PsiElement? {
    return file.elementsInRange(TextRange.create(offset - line.length, offset))
            .mapNotNull { it.getNonStrictParentOfType<KtElement>() }
            .firstOrNull()
}
