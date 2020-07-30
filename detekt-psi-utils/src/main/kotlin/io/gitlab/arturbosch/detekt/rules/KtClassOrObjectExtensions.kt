package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

fun KtClass.isClosedForExtension() = !isAbstract() && !isOpen()

fun KtClass.onlyExtendsInterfaces(): Boolean {
    return superTypeListEntries.all { it.isInterface() && " by " !in it.text }
}

private fun KtSuperTypeListEntry.isInterface(): Boolean {
    val matchingDeclaration = containingKtFile.declarations.firstOrNull { it.name == typeAsUserType?.referencedName }
    return matchingDeclaration is KtClass && matchingDeclaration.isInterface()
}

fun KtClass.extractDeclarations(): List<KtDeclaration> = body?.declarations.orEmpty()
