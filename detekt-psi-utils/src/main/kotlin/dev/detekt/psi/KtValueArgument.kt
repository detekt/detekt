package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument

fun KtValueArgument.isString(): Boolean {
    val argumentExpression = getArgumentExpression() ?: return false

    analyze(argumentExpression) {
        val type = argumentExpression.expressionType
        return argumentExpression is KtStringTemplateExpression || type != null && type.isStringType
    }
}

fun List<KtValueArgument>.isEmptyOrSingleStringArgument(): Boolean = isEmpty() || singleOrNull()?.isString() == true
