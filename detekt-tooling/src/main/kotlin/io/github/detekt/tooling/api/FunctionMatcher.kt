package io.github.detekt.tooling.api

import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

sealed class FunctionMatcher {
    abstract fun match(call: ResolvedCall<*>): Boolean

    abstract fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean

    internal data class NameOnly(
        private val fullyQualifiedName: String
    ) : FunctionMatcher() {
        override fun match(call: ResolvedCall<*>): Boolean {
            return call.resultingDescriptor.fqNameOrNull()?.asString() == fullyQualifiedName
        }

        override fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean {
            return function.name == fullyQualifiedName
        }

        override fun toString(): String {
            return fullyQualifiedName
        }
    }

    internal data class WithParameters(
        private val fullyQualifiedName: String,
        private val parameters: List<String>
    ) : FunctionMatcher() {
        override fun match(call: ResolvedCall<*>): Boolean {
            if (call.resultingDescriptor.fqNameOrNull()?.asString() != fullyQualifiedName) return false

            val encounteredParamTypes = call.candidateDescriptor.valueParameters
                .map { it.type.fqNameOrNull()?.asString() }

            return encounteredParamTypes == parameters
        }

        override fun match(function: KtNamedFunction, bindingContext: BindingContext): Boolean {
            if (bindingContext == BindingContext.EMPTY) return false
            if (function.name != fullyQualifiedName) return false

            val encounteredParameters = function.valueParameters
                .map { bindingContext[BindingContext.TYPE, it.typeReference]?.fqNameOrNull()?.toString() }

            return encounteredParameters == parameters
        }

        override fun toString(): String {
            return "$fullyQualifiedName(${parameters.joinToString()})"
        }
    }

    companion object {
        fun fromFunctionSignature(methodSignature: String): FunctionMatcher {
            val tokens = methodSignature.split("(", ")")
                .map { it.trim() }

            val methodName = tokens.first().replace("`", "")
            val params = if (tokens.size > 1) {
                tokens[1].split(",").map { it.trim() }.filter { it.isNotBlank() }
            } else {
                null
            }

            return if (params == null) {
                NameOnly(methodName)
            } else {
                WithParameters(methodName, params)
            }
        }
    }
}
