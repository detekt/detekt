package io.github.detekt.psi

import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter

sealed class FunctionMatcher {

    abstract fun match(callableDescriptor: CallableDescriptor): Boolean

    abstract fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean

    internal data class NameOnly(
        private val fullyQualifiedName: String
    ) : FunctionMatcher() {
        override fun match(callableDescriptor: CallableDescriptor): Boolean =
            callableDescriptor.fqNameSafe.asString() == fullyQualifiedName

        override fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean =
            function.name == fullyQualifiedName ||
                function.fqName?.asString() == fullyQualifiedName

        override fun toString(): String = fullyQualifiedName
    }

    internal data class WithParameters(
        private val fullyQualifiedName: String,
        private val parameters: List<String>
    ) : FunctionMatcher() {
        override fun match(callableDescriptor: CallableDescriptor): Boolean {
            val descriptor = callableDescriptor.original
            if (descriptor.fqNameSafe.asString() != fullyQualifiedName) return false

            val encounteredParamTypes =
                (listOfNotNull(descriptor.extensionReceiverParameter) + descriptor.valueParameters)
                    .map { it.type.getSignatureParameter() }

            return encounteredParamTypes == parameters
        }

        override fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean {
            if (bindingContext == BindingContext.EMPTY) return false
            if (function.name != fullyQualifiedName && function.fqName?.asString() != fullyQualifiedName) return false

            val encounteredParameters =
                (listOfNotNull(function.receiverTypeReference) + function.valueParameters.map { it.typeReference })
                    .map { bindingContext[BindingContext.TYPE, it]?.getSignatureParameter() }

            return encounteredParameters == parameters
        }

        override fun toString(): String = "$fullyQualifiedName(${parameters.joinToString()})"
    }

    companion object {
        fun fromFunctionSignature(methodSignature: String): FunctionMatcher {
            @Suppress("TooGenericExceptionCaught", "UnsafeCallOnNullableType")
            try {
                val result = functionSignatureRegex.matchEntire(methodSignature)!!

                val methodName = result.groups[1]!!.value.replace("`", "")
                val params = result.groups[2]?.value?.splitParams()
                    ?.map { changeIfLambda(it) ?: it }

                return if (params == null) {
                    NameOnly(methodName)
                } else {
                    WithParameters(methodName, params)
                }
            } catch (ex: Exception) {
                throw IllegalStateException("$methodSignature doesn't match a method signature", ex)
            }
        }
    }
}

private fun KotlinType.getSignatureParameter(): String? =
    if (isTypeParameter()) {
        toString()
    } else {
        fqNameOrNull()?.toString()
    }

// Extracted from: https://stackoverflow.com/a/16108347/842697
private fun String.splitParams(): List<String> {
    val split: MutableList<String> = mutableListOf()
    var nestingLevel = 0
    val result = StringBuilder()
    this.forEach { c ->
        if (c == ',' && nestingLevel == 0) {
            split.add(result.toString().trim())
            result.setLength(0)
        } else {
            if (c == '(') nestingLevel++
            if (c == ')') nestingLevel--
            check(nestingLevel >= 0)
            result.append(c)
        }
    }
    val lastParam = result.toString().trim()
    if (lastParam.isNotEmpty()) {
        split.add(lastParam)
    }
    return split
}

private fun changeIfLambda(param: String): String? {
    val (paramsRaw, _) = splitLambda(param) ?: return null
    val params = paramsRaw.splitParams()

    return "kotlin.Function${params.count()}"
}

private fun splitLambda(param: String): Pair<String, String>? {
    if (!param.startsWith("(")) return null

    var nestingLevel = 0
    val paramsRaw = StringBuilder()
    val returnValue = StringBuilder()

    /*
     * We don't count the first `(` so as soon as the nestingLevel reaches the last `)` we know that we read all the
     * params. Then we handle the rest of the String as the result.
     */
    param.toCharArray()
        .drop(1)
        .forEach { c ->
            if (nestingLevel >= 0) {
                if (c == '(') nestingLevel++
                if (c == ')') nestingLevel--
                if (nestingLevel >= 0) {
                    paramsRaw.append(c)
                }
            } else {
                returnValue.append(c)
            }
        }

    check(returnValue.trim().startsWith("->"))

    return paramsRaw.toString().trim() to returnValue.toString().substringAfter("->").trim()
}

private val functionSignatureRegex = """((?:[^()`]|`.*`)*)(?:\((.*)\))?""".toRegex()
