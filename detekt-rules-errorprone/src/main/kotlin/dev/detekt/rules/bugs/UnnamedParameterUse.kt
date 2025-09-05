package dev.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.FunctionMatcher
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgument

/**
 * Reports usage of unnamed parameter. Passing parameters without name can cause issue when parameters order of same
 * type changes. And code gets error prone as it gets easy to mix up parameters of the same type
 *
 * <noncompliant>
 * fun log(enabled: Boolean, shouldLog: Boolean) {
 *     if (shouldLog) println(enabled)
 * }
 * fun test() {
 *     log(false, true)
 * }
 *
 * // allowAdjacentDifferentTypeParams = false
 * fun logMsg(msg: String, shouldLog: Boolean) {
 *    if(shouldLog) println(msg)
 * }
 * fun test() {
 *     logMsg("test", true)
 * }
 *
 * // allowSingleParamUse = false and allowAdjacentDifferentTypeParams = false
 * fun logMsg(msg: String) {
 *     println(msg)
 * }
 * fun test() {
 *     logMsg("test")
 * }
 *
 * // ignoreArgumentsMatchingNames = false
 * fun test(enabled: Boolean, shouldLog: Boolean) {
 *     log(enabled, shouldLog)
 * }
 *
 * </noncompliant>
 *
 * <compliant>
 * fun log(enabled: Boolean, shouldLog: Boolean) {
 *     if (shouldLog) println(enabled)
 * }
 * fun test() {
 *     log(enabled = false, shouldLog = true)
 * }
 * // ignoreArgumentsMatchingNames = true
 * fun test(enabled: Boolean, shouldLog: Boolean) {
 *     log(enabled, shouldLog)
 * }
 *
 * // allowAdjacentDifferentTypeParams = true
 * fun logMsg(msg: String, shouldLog: Boolean) {
 *    if(shouldLog) println(msg)
 * }
 * fun test() {
 *     logMsg("test", true)
 * }
 *
 * // allowSingleParamUse = true
 * fun logMsg(msg: String) {
 *     println(msg)
 * }
 * fun test() {
 *     logMsg("test")
 * }
 * </compliant>
 */
class UnnamedParameterUse(config: Config) :
    Rule(
        config,
        "Passing no named parameters can cause issue when parameters order change"
    ),
    RequiresAnalysisApi {

    @Configuration("Allow adjacent unnamed params when type of parameters can not be assigned to each other")
    val allowAdjacentDifferentTypeParams: Boolean by config(true)

    @Configuration("Allow single unnamed parameter use")
    val allowSingleParamUse: Boolean by config(true)

    @Configuration("ignores when argument values are the same as the parameter names")
    private val ignoreArgumentsMatchingNames: Boolean by config(true)

    @Configuration(
        "List of function signatures which should be ignored by this rule. " +
            "Specifying fully-qualified function signature with name only (i.e. `kotlin.collections.maxOf`) will " +
            "ignore all function calls matching the name. Specifying fully-qualified function signature with " +
            "parameters (i.e. `kotlin.collections.maxOf(kotlin.Long, kotlin.Long)`) will ignore only " +
            "function calls matching the name and parameters exactly."
    )
    private val ignoreFunctionCall: List<FunctionMatcher> by config(emptyList<String>()) {
        it.map(FunctionMatcher::fromFunctionSignature)
    }

    @Suppress("ReturnCount", "CyclomaticComplexMethod")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val valueArgumentList = expression.valueArgumentList ?: return
        if (valueArgumentList.arguments.isEmpty()) {
            return
        }

        analyze(expression) {
            val call = expression.resolveToCall()?.singleFunctionCallOrNull() ?: return
            val symbol = call.symbol
            if (!symbol.hasStableParameterNames) return
            if (symbol.origin.let { it == KaSymbolOrigin.JAVA_SOURCE || it == KaSymbolOrigin.JAVA_LIBRARY }) return
            if (ignoreFunctionCall.any { it.match(symbol) }) return

            val argumentToParameterMap = valueArgumentList.arguments.associateWith {
                val parameter = call.argumentMapping[it.getArgumentExpression()]?.symbol
                ParamInfo(
                    name = parameter?.name,
                    isNamed = it.isNamed(),
                    isVararg = parameter?.isVararg == true,
                    isSameNamed = parameter?.run { name.asString() } == it.getArgumentExpression()?.text,
                )
            }
            if (allowSingleParamUse && argumentToParameterMap.values.distinct().size <= 1) return

            if (allowAdjacentDifferentTypeParams &&
                argumentToParameterMap
                    .entries
                    .windowed(2)
                    .all { isAdjacentUnnamedParamsAllowed(it) }
            ) {
                return
            }

            if (argumentToParameterMap.values.any {
                    if (ignoreArgumentsMatchingNames) {
                        !(it.isNamed || it.isSameNamed)
                    } else {
                        !it.isNamed
                    }
                }
            ) {
                val target = expression.calleeExpression ?: expression
                val message =
                    "Consider using named parameters in ${target.text} as they make usage of the function more safe."
                report(Finding(Entity.from(target), message))
            }
        }
    }

    private fun KaSession.isAdjacentUnnamedParamsAllowed(
        paramInfos: List<Map.Entry<KtValueArgument, ParamInfo>>,
    ): Boolean {
        fun ParamInfo.isNamedOrVararg() = this.isNamed || this.isVararg
        val (firstEntry, secondEntry) = paramInfos
        if (
            ignoreArgumentsMatchingNames &&
            (firstEntry.value.isSameNamed || secondEntry.value.isSameNamed)
        ) {
            // if there is any matching name param
            return true
        }
        // if both param has same name, then order doesn't matter
        return (firstEntry.key.text == secondEntry.key.text) ||
            // any of these are either has name or vararg
            (firstEntry.value.isNamedOrVararg() || secondEntry.value.isNamedOrVararg()) ||
            (typeCanBeAssigned(firstEntry.key, secondEntry.key).not())
    }

    @Suppress("ReturnCount")
    private fun KaSession.typeCanBeAssigned(
        firstParam: KtValueArgument,
        secondParam: KtValueArgument,
    ): Boolean {
        val param1Type = firstParam.getArgumentExpression()?.expressionType ?: return true
        val param2Type = secondParam.getArgumentExpression()?.expressionType ?: return true
        return param1Type.isSubtypeOf(param2Type) || param2Type.isSubtypeOf(param1Type)
    }

    private data class ParamInfo(
        val name: Name?,
        val isNamed: Boolean,
        val isVararg: Boolean,
        val isSameNamed: Boolean,
    )
}
