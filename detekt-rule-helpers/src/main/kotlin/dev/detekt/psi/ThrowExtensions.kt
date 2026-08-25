package dev.detekt.psi

import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType

fun KtThrowExpression.isIllegalStateException() = isExceptionOfType<IllegalStateException>()

fun KtThrowExpression.isIllegalArgumentException() = isExceptionOfType<IllegalArgumentException>()

inline fun <reified T : Exception> KtThrowExpression.isExceptionOfType(): Boolean =
    findDescendantOfType<KtCallExpression>()?.firstChild?.text == T::class.java.simpleName

val KtThrowExpression.arguments: List<KtValueArgument>
    get() = findDescendantOfType<KtCallExpression>()?.valueArguments.orEmpty()

fun KtThrowExpression.isEnclosedByConditionalStatement(): Boolean =
    when (parent) {
        is KtContainerNodeForControlStructureBody -> true
        is KtBlockExpression -> parent.parent is KtContainerNodeForControlStructureBody
        else -> false
    }
