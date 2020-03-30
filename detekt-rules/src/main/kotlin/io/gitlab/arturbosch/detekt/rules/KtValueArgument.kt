package io.gitlab.arturbosch.detekt.rules

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

fun KtValueArgument.isString(bindingContext: BindingContext): Boolean {
    return if (bindingContext != BindingContext.EMPTY) {
        val descriptor =
            getArgumentExpression()?.getResolvedCall(bindingContext)?.resultingDescriptor as? ValueParameterDescriptor
        val type = descriptor?.type
        type != null && KotlinBuiltIns.isString(type)
    } else {
        true
    }
}

fun List<KtValueArgument>.isEmptyOrSingleStringArgument(bindingContext: BindingContext): Boolean =
    isEmpty() || singleOrNull()?.isString(bindingContext) == true
