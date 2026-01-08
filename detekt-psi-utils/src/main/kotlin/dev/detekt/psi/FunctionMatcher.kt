package dev.detekt.psi

import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertyGetterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySetterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtNamedFunction

sealed class FunctionMatcher {

    abstract fun match(function: KtNamedFunction, fullAnalysis: Boolean): Boolean

    abstract fun match(propertySymbol: KaPropertySymbol?, symbol: KaCallableSymbol): Boolean
    abstract fun match(symbol: KaCallableSymbol): Boolean

    internal data class NameOnly(private val fullyQualifiedName: String) : FunctionMatcher() {

        override fun match(function: KtNamedFunction, fullAnalysis: Boolean): Boolean =
            function.name == fullyQualifiedName ||
                function.fqName?.asString() == fullyQualifiedName

        override fun match(propertySymbol: KaPropertySymbol?, symbol: KaCallableSymbol): Boolean =
            if (propertySymbol != null) {
                getNameForGetterOrSetter(
                    propertySymbol,
                    symbol
                ) == fullyQualifiedName
            } else {
                match(symbol)
            }

        override fun match(symbol: KaCallableSymbol): Boolean = symbol.asFqNameString() == fullyQualifiedName

        override fun toString(): String = fullyQualifiedName
    }

    internal data class WithParameters(private val fullyQualifiedName: String, private val parameters: List<String>) :
        FunctionMatcher() {

        override fun match(function: KtNamedFunction, fullAnalysis: Boolean): Boolean {
            if (!fullAnalysis) return false
            if (function.name != fullyQualifiedName && function.fqName?.asString() != fullyQualifiedName) return false

            val encounteredParameters = buildList {
                analyze(function) {
                    addIfNotNull(function.receiverTypeReference?.run { type.asFqNameString() })
                    addAll(
                        function.valueParameters.map {
                            if (it.isVarArg) {
                                "vararg ${it.returnType.arrayElementType?.asFqNameString()}"
                            } else {
                                it.returnType.asFqNameString()
                            }
                        }
                    )
                }
            }

            return encounteredParameters == parameters
        }

        override fun match(propertySymbol: KaPropertySymbol?, symbol: KaCallableSymbol): Boolean =
            if (propertySymbol != null) {
                getNameForGetterOrSetter(propertySymbol, symbol) == fullyQualifiedName
            } else {
                match(symbol)
            }

        override fun match(symbol: KaCallableSymbol): Boolean {
            if (symbol.asFqNameString() != fullyQualifiedName) return false

            symbol as KaFunctionSymbol
            val encounteredParamTypes = getParams(symbol)

            return encounteredParamTypes == parameters
        }

        private fun getParams(symbol: KaFunctionSymbol): List<String?> {
            val encounteredParamTypes = buildList {
                addIfNotNull(symbol.receiverParameter?.returnType?.asFqNameString())
                addAll(
                    symbol.valueParameters.map { value ->
                        if (value.isVararg) {
                            "vararg ${value.returnType.asFqNameString()}"
                        } else {
                            value.returnType.asFqNameString()
                        }
                    }
                )
            }
            return encounteredParamTypes
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

        fun getNameForGetterOrSetter(propertySymbol: KaPropertySymbol, symbol: KaCallableSymbol): String? {
            return if (symbol.callableId != null) {
                // in case it is Java getter or setter then callableId id will be not null and can be used
                symbol.asFqNameString()
            } else {
                // when it's a Kotlin property and getX and setX synthetic methods are only from Java side
                val callableId = propertySymbol.callableId
                val propertyName = callableId?.callableName?.asString()
                if (propertyName.isNullOrEmpty()) return null
                val capitalPropertyName = propertyName[0].uppercaseChar() + propertyName.substring(1)
                if (symbol is KaPropertyGetterSymbol || symbol is KaPropertySetterSymbol) {
                    val getterOrSetterName = if (symbol is KaPropertyGetterSymbol) "get" else "set"
                    callableId
                        .copy(
                            Name.identifier("$getterOrSetterName$capitalPropertyName")
                        )
                        .asSingleFqName()
                        .asString()
                } else {
                    null
                }
            }
        }
    }
}

private fun <T> MutableCollection<T>.addIfNotNull(t: T) {
    if (t != null) add(t)
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

private fun KaCallableSymbol.asFqNameString() =
    if (this is KaConstructorSymbol) {
        returnType.asFqNameString().plus(".<init>")
    } else {
        callableId?.run { asSingleFqName().asString() } ?: returnType.asFqNameString()
    }

private fun KaType.asFqNameString() =
    symbol?.classId?.asFqNameString() ?: toString().replace('/', '.').removeSuffix("!")

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
