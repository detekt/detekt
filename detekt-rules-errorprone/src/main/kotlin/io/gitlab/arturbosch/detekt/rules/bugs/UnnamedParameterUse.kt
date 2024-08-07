package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
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
 * fun log(msg: String, shouldLog: Boolean) {
 *     println(msg)
 * }
 * fun test() {
 *     log("test", true)
 * }
 *
 * // allowSingleParamUse = false and allowAdjacentDifferentTypeParams = false
 * fun log(msg: String) {
 *     println(msg)
 * }
 * fun test() {
 *     log("test")
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun log(enabled: Boolean, shouldLog: Boolean) {
 *     if (shouldLog) println(enabled)
 * }
 * fun test() {
 *     log(enabled = false, shouldLog = true)
 * }
 *
 * // allowAdjacentDifferentTypeParams = true
 * fun log(msg: String, shouldLog: Boolean) {
 *     println(msg)
 * }
 * fun test() {
 *     log("test", true)
 * }
 *
 * // allowSingleParamUse = true
 * fun log(msg: String) {
 *     println(msg)
 * }
 * fun test() {
 *     log("test")
 * }
 * </compliant>
 */
class UnnamedParameterUse(config: Config) :
    Rule(
        config,
        "Passing no named parameters can cause issue when parameters order change"
    ),
    RequiresTypeResolution {
    @Configuration("Allow adjacent unnamed params when type of parameters can not be assigned to each other")
    val allowAdjacentDifferentTypeParams: Boolean by config(true)

    @Configuration("Allow single unnamed parameter use")
    val allowSingleParamUse: Boolean by config(true)

    @Suppress("ReturnCount")
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
        val paramDescriptorToArgumentMap: Map<KtValueArgument, ValueParameterDescriptor?> =
            valueArgumentList.arguments.associateWith {
                callDescriptor.getParameterForArgument(it)
            }
        if (allowSingleParamUse && paramDescriptorToArgumentMap.values.distinct().size <= 1) {
            return
        }

        val namedArgumentList = valueArgumentList.arguments.map {
            val isNamedOrVararg = it.isNamed() || paramDescriptorToArgumentMap[it]?.isVararg == true
            // No name parameter if it is vararg
            isNamedOrVararg to it
        }

        if (allowAdjacentDifferentTypeParams && namedArgumentList.windowed(2).all(::isAdjacentUnnamedParamsAllowed)) {
            return
        }

        if (namedArgumentList.any { it.first.not() }) {
            val target = expression.calleeExpression ?: expression
            report(
                CodeSmell(
                    Entity.from(target),
                    "Consider using named parameters in ${target.text} as they make usage of the function more safe."
                )
            )
        }
    }

    private fun isAdjacentUnnamedParamsAllowed(it: List<Pair<Boolean, KtValueArgument>>) =
        (it[0].second.text == it[1].second.text) ||
            (it[0].first || it[1].first) ||
            (typeCanBeAssigned(it[0].second, it[1].second).not())

    @Suppress("ReturnCount")
    private fun typeCanBeAssigned(firstParam: KtValueArgument, secondParam: KtValueArgument): Boolean {
        val param1Type =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, firstParam.getArgumentExpression()]?.type ?: return true
        val param2Type =
            bindingContext[BindingContext.EXPRESSION_TYPE_INFO, secondParam.getArgumentExpression()]?.type
                ?: return true

        return param1Type.isSubtypeOf(param2Type) || param2Type.isSubtypeOf(param1Type)
    }
}
