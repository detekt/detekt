package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleConstructorCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.singleVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.ArrayFqNames

/**
 * In most cases using a spread operator causes a full copy of the array to be created before calling a method.
 * This has a very high performance penalty. Benchmarks showing this performance penalty can be seen here:
 * [Exploring Kotlin Hidden Costs - Part 1](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-1-fbb9935d9b62)
 * [Exploring Kotlin Hidden Costs - Part 2](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-2-324a4a50b70)
 * [Exploring Kotlin Hidden Costs - Part 3](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-3-3bf6e0dbf0a4)
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
    RequiresAnalysisApi {

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
            Finding(
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
        val expression = getArgumentExpression() ?: return false
        analyze(expression) {
            val call = expression.resolveToCall() ?: return false

            if ((call.singleVariableAccessCall()?.symbol as? KaValueParameterSymbol)?.isVararg == true) {
                return true // As of Kotlin 1.1.60 passing varargs parameters to vararg calls does not create a new copy
            }

            if (call.singleConstructorCallOrNull() != null) return true

            val fqName = call.singleFunctionCallOrNull()?.symbol?.callableId?.asSingleFqName()
            return fqName in ArrayFqNames.ARRAY_CALL_FQ_NAMES || fqName == FqName("kotlin.arrayOfNulls")
        }
    }
}
