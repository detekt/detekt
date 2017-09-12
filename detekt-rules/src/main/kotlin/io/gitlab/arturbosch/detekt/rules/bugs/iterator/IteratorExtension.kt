package io.gitlab.arturbosch.detekt.rules.bugs.iterator

import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

internal fun KtClassOrObject.isImplementingIterator(): Boolean {
	val typeList = this.getSuperTypeList()?.entries
	val name = typeList?.firstOrNull()?.typeAsUserType?.referencedName
	return name == "Iterator"
}

internal fun KtClassOrObject.getMethod(name: String): KtNamedFunction? {
	val functions = this.declarations.filterIsInstance(KtNamedFunction::class.java)
	return functions.firstOrNull { it.name == name && it.valueParameters.isEmpty() }
}
