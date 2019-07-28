package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

fun KtClassOrObject.isInterface(): Boolean {
    return (this as? KtClass)?.isInterface() == true
}

fun KtClass.doesNotExtendAnything() = superTypeListEntries.isEmpty()

fun KtClass.isClosedForExtension() = !isAbstract() && !isOpen()

fun KtClass.extractDeclarations(): List<KtDeclaration> = body?.declarations.orEmpty()
