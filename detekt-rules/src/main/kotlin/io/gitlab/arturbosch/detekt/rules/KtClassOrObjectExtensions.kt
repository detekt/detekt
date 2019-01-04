package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject

fun KtClassOrObject.isInterface(): Boolean {
	return (this as? KtClass)?.isInterface() == true
}
