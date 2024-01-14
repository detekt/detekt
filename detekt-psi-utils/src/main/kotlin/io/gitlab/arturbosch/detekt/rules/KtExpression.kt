package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypesAndPredicate

fun KtExpression.isUnitExpression() = text == StandardNames.FqNames.unit.shortName().asString()

fun PsiElement.getParentExpressionAfterParenthesis(strict: Boolean = true): PsiElement? =
    this.getParentOfTypesAndPredicate(
        strict,
        PsiElement::class.java,
    ) { it !is KtParenthesizedExpression }
