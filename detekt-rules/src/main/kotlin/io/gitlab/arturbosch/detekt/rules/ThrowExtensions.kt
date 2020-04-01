package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

internal fun KtThrowExpression.isIllegalStateException() =
    isExceptionOfType<IllegalStateException>()

internal fun KtThrowExpression.isIllegalArgumentException() =
    isExceptionOfType<IllegalArgumentException>()

internal inline fun <reified T : Exception> KtThrowExpression.isExceptionOfType(): Boolean {
    return findDescendantOfType<KtCallExpression>()?.firstChild?.text == T::class.java.simpleName
}

internal val KtThrowExpression.arguments: List<KtValueArgument>
    get() = findDescendantOfType<KtCallExpression>()?.valueArguments.orEmpty()

internal fun KtThrowExpression.isEnclosedByConditionalStatement(): Boolean {
    return parent is KtIfExpression || parent is KtContainerNodeForControlStructureBody
}
