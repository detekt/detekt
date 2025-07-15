package io.gitlab.arturbosch.detekt.rules.bugs

import io.github.detekt.psi.FunctionMatcher
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.load.java.isFromJava
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

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
    RequiresFullAnalysis {

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
        val callDescriptor = expression.getResolvedCall(bindingContext) ?: return
        if ((callDescriptor.resultingDescriptor as? CallableMemberDescriptor)?.isFromJava == true) {
            return
        }
        if (ignoreFunctionCall.any { it.match(callDescriptor.resultingDescriptor) }) return
        val paramDescriptorToArgumentMap =
            valueArgumentList.arguments.associateWith {
                val valueParamDescriptor = callDescriptor.getParameterForArgument(it)
                ParamInfo(
                    isNamed = it.isNamed(),
                    isVararg = valueParamDescriptor?.isVararg == true,
                    valueParaDescriptor = valueParamDescriptor,
                    isSameNamed = valueParamDescriptor?.run { name.asString() } == it.getArgumentExpression()?.text
                )
            }
        if (allowSingleParamUse && paramDescriptorToArgumentMap.values.distinct().size <= 1) {
            return
        }

        if (
            allowAdjacentDifferentTypeParams &&
            paramDescriptorToArgumentMap
                .entries
                .windowed(2)
                .all(::isAdjacentUnnamedParamsAllowed)
        ) {
            return
        }

        if (
            paramDescriptorToArgumentMap.values.any {
                if (ignoreArgumentsMatchingNames) {
                    !(it.isNamed || it.isSameNamed)
                } else {
                    !it.isNamed
                }
            }
        ) {
            val target = expression.calleeExpression ?: expression
            report(
                Finding(
                    Entity.from(target),
                    "Consider using named parameters in ${target.text} as they make usage of the function more safe."
                )
            )
        }
    }

    private fun isAdjacentUnnamedParamsAllowed(paramInfos: List<Map.Entry<KtValueArgument, ParamInfo>>): Boolean {
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
    private fun typeCanBeAssigned(
        firstParam: KtValueArgument,
        secondParam: KtValueArgument,
    ): Boolean {
        val param1Type =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, firstParam.getArgumentExpression()]?.type
                ?: return true
        val param2Type =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, secondParam.getArgumentExpression()]?.type
                ?: return true

        return param1Type.isSubtypeOf(param2Type) || param2Type.isSubtypeOf(param1Type)
    }

    private data class ParamInfo(
        val valueParaDescriptor: ValueParameterDescriptor?,
        val isNamed: Boolean,
        val isVararg: Boolean,
        val isSameNamed: Boolean,
    )
}
