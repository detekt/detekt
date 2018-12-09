package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import kotlin.reflect.KClass

/**
 * @author Artur Bosch
 */

/**
 * Tests if this element is part of given PsiElement.
 */
fun PsiElement.isPartOf(clazz: KClass<out PsiElement>) = getNonStrictParentOfType(clazz.java) != null

/**
 * Tests of this element is part of a kotlin string.
 */
fun PsiElement.isPartOfString() = isPartOf(KtStringTemplateEntry::class)
