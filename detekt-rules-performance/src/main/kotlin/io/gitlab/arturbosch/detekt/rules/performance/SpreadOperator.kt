package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.CompileTimeConstantUtils
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.components.isVararg
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

/**
 * In most cases using a spread operator causes a full copy of the array to be created before calling a method.
 * This has a very high performance penalty. Benchmarks showing this performance penalty can be seen here:
 * https://web.archive.org/web/20230514162525/https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 *
 * The Kotlin compiler since v1.1.60 has an optimization that skips the array copy when an array constructor
 * function is used to create the arguments that are passed to the vararg parameter. This case will not be flagged
 * by the rule since it doesn't suffer the performance penalty of an array copy.
 *
 * <noncompliant>
 * val strs = arrayOf("value one", "value two")
 * val foo = bar(*strs)
 *
 * fun bar(vararg strs: String) {
 *     strs.forEach { println(it) }
 * }
 * </noncompliant>
 *
 * <compliant>
 * // array copy skipped in this case since Kotlin 1.1.60
 * val foo = bar(*arrayOf("value one", "value two"))
 *
 * // array not passed so no array copy is required
 * val foo2 = bar("value one", "value two")
 *
 * fun bar(vararg strs: String) {
 *     strs.forEach { println(it) }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.0.0")
class SpreadOperator(config: Config) :
    Rule(
        config,
        "In most cases using a spread operator causes a full copy of the array to be created before calling a " +
            "method. This may result in a performance penalty."
    ),
    RequiresTypeResolution {
    override fun visitValueArgumentList(list: KtValueArgumentList) {
        super.visitValueArgumentList(list)
        list.arguments
            .filter { it.isSpread }
            .filterNotNull()
            .forEach { checkCanSkipArrayCopy(it, list) }
    }

    private fun checkCanSkipArrayCopy(arg: KtValueArgument, argsList: KtValueArgumentList) {
        if (arg.canSkipArrayCopyForSpreadArgument()) {
            return
        }
        report(
            CodeSmell(
                Entity.from(argsList),
                "Used in this way a spread operator causes a full copy of the array to be created before " +
                    "calling a method. This may result in a performance penalty."
            )
        )
    }

    /**
     * Checks if an array copy can be skipped for this usage of the spread operator. If not, an array copy is required
     * for this usage of the spread operator, which will have a performance impact.
     */
    private fun KtValueArgument.canSkipArrayCopyForSpreadArgument(): Boolean {
        val resolvedCall = getArgumentExpression().getResolvedCall(bindingContext) ?: return false
        val calleeDescriptor = resolvedCall.resultingDescriptor
        if (calleeDescriptor is ParameterDescriptor && calleeDescriptor.isVararg) {
            return true // As of Kotlin 1.1.60 passing varargs parameters to vararg calls does not create a new copy
        }
        return calleeDescriptor is ConstructorDescriptor ||
            CompileTimeConstantUtils.isArrayFunctionCall(resolvedCall) ||
            DescriptorUtils.getFqName(calleeDescriptor).asString() == "kotlin.arrayOfNulls"
    }
}
