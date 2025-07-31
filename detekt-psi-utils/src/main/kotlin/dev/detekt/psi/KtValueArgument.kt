package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

fun KtValueArgument.isString(bindingContext: BindingContext): Boolean {
    val argumentExpression = getArgumentExpression()
    return if (bindingContext != BindingContext.EMPTY) {
        val type = argumentExpression?.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType
        argumentExpression is KtStringTemplateExpression || type != null && KotlinBuiltIns.isString(type)
    } else {
        argumentExpression is KtStringTemplateExpression
    }
}

fun KtValueArgument.isString(): Boolean {
    val argumentExpression = getArgumentExpression() ?: return false

    analyze(argumentExpression) {
        val type = argumentExpression.expressionType
        return argumentExpression is KtStringTemplateExpression || type != null && type.isStringType
    }
}

fun List<KtValueArgument>.isEmptyOrSingleStringArgument(bindingContext: BindingContext): Boolean =
    isEmpty() || singleOrNull()?.isString(bindingContext) == true

fun List<KtValueArgument>.isEmptyOrSingleStringArgument(): Boolean =
    isEmpty() || singleOrNull()?.isString() == true
