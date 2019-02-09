package io.gitlab.arturbosch.detekt.rules

import kotlin.reflect.KClass
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */

/**
 * Tests if this element is part of given PsiElement.
 */
fun PsiElement.isPartOf(clazz: KClass<out PsiElement>): Boolean = getNonStrictParentOfType(clazz.java) != null

/**
 * Tests if this element is part of a kotlin string.
 */
fun PsiElement.isPartOfString(): Boolean = isPartOf(KtStringTemplateEntry::class)
