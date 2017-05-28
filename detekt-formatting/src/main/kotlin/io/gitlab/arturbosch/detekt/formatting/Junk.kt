@file:Suppress("TooManyFunctions")

package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.isPartOfString
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.psiUtil.startOffset

internal fun PsiElement.isNotPartOfString() = !isPartOfString()
internal fun PsiElement.isNotPartOfEnum() = getNonStrictParentOfType(KtEnumEntry::class.java) == null

internal fun <T> List<T>.dropLast() = this.subList(0, this.size - 1)
internal fun <T> List<T>.dropFirst() = this.subList(1, this.size)

internal fun PsiElement.startAndEndLine(): Pair<Int, Int> =
		Location.startLineAndColumn(this).line to
				Location.startLineAndColumn(this, endOffset - startOffset).line

internal fun PsiElement.nextLeafIsWhiteSpace() = nextLeaf() is PsiWhiteSpace
internal fun PsiElement.isNewLine() = text.contains("\n")
internal fun PsiElement.isSemicolon() = textMatches(";")
internal fun PsiElement.isDoubleSemicolon() = textMatches(";;")
