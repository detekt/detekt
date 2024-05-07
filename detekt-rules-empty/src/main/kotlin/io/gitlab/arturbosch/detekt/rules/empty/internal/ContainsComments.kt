package io.gitlab.arturbosch.detekt.rules.empty.internal

import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

fun KtClassOrObject.hasCommentInside() = this.body?.hasCommentInside() ?: false

fun PsiElement.hasCommentInside(): Boolean = getChildrenOfType<PsiComment>().isNotEmpty()
