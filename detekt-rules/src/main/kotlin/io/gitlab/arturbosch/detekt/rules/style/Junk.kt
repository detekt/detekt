package io.gitlab.arturbosch.detekt.rules.style

import org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.elementsInRange

internal fun findAnnotatedStatementInLine(file: KtFile, offset: Int, line: String): PsiElement? {
    return file.elementsInRange(TextRange.create(offset - line.length, offset))
            .mapNotNull { it as? KtAnnotated ?: if (it.parent is KtAnnotated) it.parent else null }
            .firstOrNull()
}
