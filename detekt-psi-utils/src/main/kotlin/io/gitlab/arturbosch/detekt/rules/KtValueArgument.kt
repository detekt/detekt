package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

fun KtValueArgument.isString(bindingContext: BindingContext): Boolean {
    val argumentExpression = getArgumentExpression()
    return if (bindingContext != BindingContext.EMPTY) {
        val type = argumentExpression?.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType
        argumentExpression is KtStringTemplateExpression || type != null && KotlinBuiltIns.isString(type)
    } else {
        argumentExpression is KtStringTemplateExpression
    }
}

fun List<KtValueArgument>.isEmptyOrSingleStringArgument(bindingContext: BindingContext): Boolean =
    isEmpty() || singleOrNull()?.isString(bindingContext) == true
