package io.gitlab.arturbosch.detekt.rules.bugs.util

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

internal fun KtClassOrObject.isImplementingIterator(): Boolean {
    val typeList = this.getSuperTypeList()?.entries
    val name = typeList?.firstOrNull()?.typeAsUserType?.referencedName
    return name == "Iterator"
}

internal fun KtClassOrObject.getMethod(name: String): KtNamedFunction? {
    val functions = this.declarations.filterIsInstance<KtNamedFunction>()
    return functions.firstOrNull { it.name == name && it.valueParameters.isEmpty() }
}

internal fun KtNamedFunction.throwsNoSuchElementExceptionThrown(): Boolean {
    return this.bodyExpression
            ?.anyDescendantOfType<KtThrowExpression> { isNoSuchElementExpression(it) } == true
}

private fun isNoSuchElementExpression(expression: KtThrowExpression): Boolean {
    val calleeExpression = (expression.thrownExpression as? KtCallExpression)?.calleeExpression
    return calleeExpression?.text == "NoSuchElementException"
}
