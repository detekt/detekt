package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtUserType

fun KtAnnotated.hasAnnotation(
    vararg annotationNames: String,
): Boolean {
    val names = annotationNames.toHashSet()
    val predicate: (KtAnnotationEntry) -> Boolean = {
        it.typeReference
            ?.typeElement
            ?.let { ktTypeElement -> ktTypeElement as? KtUserType }
            ?.referencedName in names
    }
    return annotationEntries.any(predicate)
}
