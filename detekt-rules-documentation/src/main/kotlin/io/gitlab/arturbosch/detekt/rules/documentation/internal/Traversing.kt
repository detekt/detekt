package io.gitlab.arturbosch.detekt.rules.documentation.internal

import dev.detekt.psi.isProtected
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPublic

fun KtNamedDeclaration.isPublicInherited(): Boolean = isPublicInherited(false)

fun KtNamedDeclaration.isPublicInherited(considerProtectedAsPublic: Boolean): Boolean {
    var classOrObject = containingClassOrObject
    while (classOrObject != null) {
        if (!classOrObject.isPublic && !(considerProtectedAsPublic && classOrObject.isProtected())) {
            return false
        }
        classOrObject = classOrObject.containingClassOrObject
    }
    return true
}
