package dev.detekt.rules.empty.internal

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

fun KtClassOrObject.hasCommentInside() = this.body?.hasCommentInside() ?: false

fun PsiElement.hasCommentInside(): Boolean = getChildrenOfType<PsiComment>().isNotEmpty()
