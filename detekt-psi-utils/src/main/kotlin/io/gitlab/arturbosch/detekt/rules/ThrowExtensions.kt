package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

fun KtThrowExpression.isIllegalStateException() =
    isExceptionOfType<IllegalStateException>()

fun KtThrowExpression.isIllegalArgumentException() =
    isExceptionOfType<IllegalArgumentException>()

inline fun <reified T : Exception> KtThrowExpression.isExceptionOfType(): Boolean {
    return findDescendantOfType<KtCallExpression>()?.firstChild?.text == T::class.java.simpleName
}

val KtThrowExpression.arguments: List<KtValueArgument>
    get() = findDescendantOfType<KtCallExpression>()?.valueArguments.orEmpty()

fun KtThrowExpression.isEnclosedByConditionalStatement(): Boolean {
    return parent is KtIfExpression || parent is KtContainerNodeForControlStructureBody
}
