package io.gitlab.arturbosch.detekt.rules.bugs.util

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

internal fun KtNamedDeclaration.throwsNoSuchElementExceptionThrown() =
    anyDescendantOfType<KtThrowExpression> { isNoSuchElementExpression(it) }

private fun isNoSuchElementExpression(expression: KtThrowExpression): Boolean {
    val calleeExpression = (expression.thrownExpression as? KtCallExpression)?.calleeExpression
    return calleeExpression?.text == "NoSuchElementException"
}
