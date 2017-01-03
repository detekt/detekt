package io.gitlab.arturbosch.detekt.formatting

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import kotlin.reflect.KClass

internal fun PsiElement.isPartOf(clazz: KClass<out PsiElement>) = getNonStrictParentOfType(clazz.java) != null
internal fun PsiElement.isPartOfString() = isPartOf(org.jetbrains.kotlin.psi.KtStringTemplateEntry::class)

internal fun <T> List<T>.head() = this.subList(0, this.size - 1)
internal fun <T> List<T>.tail() = this.subList(1, this.size)