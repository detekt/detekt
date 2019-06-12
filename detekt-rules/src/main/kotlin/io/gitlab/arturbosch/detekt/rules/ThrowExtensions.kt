package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import kotlin.reflect.KClass

internal fun KtThrowExpression.isIllegalStateException() =
    isExceptionOfType(IllegalStateException::class)

internal fun KtThrowExpression.isIllegalArgumentException() =
    isExceptionOfType(IllegalArgumentException::class)

internal fun <T : Exception> KtThrowExpression.isExceptionOfType(clazz: KClass<T>): Boolean {
    return findDescendantOfType<KtCallExpression>()?.firstChild?.text == clazz.java.simpleName
}

internal val KtThrowExpression.argumentCount
    get() = findDescendantOfType<KtCallExpression>()?.valueArgumentList?.children?.size ?: 0

internal fun KtThrowExpression.isAfterAPreCondition(): Boolean {
    return parent is KtIfExpression || parent is KtContainerNodeForControlStructureBody
}

internal fun KtThrowExpression.isInAWhenElseBranch(): Boolean {
    val parentExpression = parent
    return parentExpression is KtWhenEntry && parentExpression.isElse
}
