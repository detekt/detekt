package dev.detekt.psi

import org.jetbrains.kotlin.idea.references.KtReference
import org.jetbrains.kotlin.psi.KtElement

fun KtElement.mainReference(): KtReference? = references.firstNotNullOfOrNull { it as? KtReference }
