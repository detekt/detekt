package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.CompileTimeConstantUtils
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

/**
 * In most cases using a spread operator causes a full copy of the array to be created before calling a method.
 * This has a very high performance penalty. Benchmarks showing this performance penalty can be seen here:
 * https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
 *
 * The Kotlin compiler since v1.1.60 has an optimization that skips the array copy when an array constructor
 * function is used to create the arguments that are passed to the vararg parameter. When type resolution is enabled in
 * detekt this case will not be flagged by the rule since it doesn't suffer the performance penalty of an array copy.
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
 *
 * @active since v1.0.0
 */
class SpreadOperator(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("SpreadOperator", Severity.Performance,
            "In most cases using a spread operator causes a full copy of the array to be created before calling a " +
                    "method which has a very high performance penalty.",
            Debt.TWENTY_MINS)

    override fun visitValueArgumentList(list: KtValueArgumentList) {
        super.visitValueArgumentList(list)
        list.arguments
            .filter { it.isSpread }
            .filterNotNull()
            .forEach {
                if (bindingContext == BindingContext.EMPTY) {
                    report(CodeSmell(issue, Entity.from(list), issue.description))
                    return
                }
                if (!it.canSkipArrayCopyForSpreadArgument()) {
                    report(
                        CodeSmell(
                            issue,
                            Entity.from(list),
                            "Used in this way a spread operator causes a full copy of the array to be created before " +
                                    "calling a method which has a very high performance penalty."
                        )
                    )
                }
            }
    }

    /**
     * Checks if an array copy can be skipped for this usage of the spread operator. If not, an array copy is required
     * for this usage of the spread operator, which will have a performance impact.
     */
    private fun KtValueArgument.canSkipArrayCopyForSpreadArgument(): Boolean {
        val resolvedCall = getArgumentExpression().getResolvedCall(bindingContext) ?: return false
        val calleeDescriptor = resolvedCall.resultingDescriptor
        return calleeDescriptor is ConstructorDescriptor ||
                CompileTimeConstantUtils.isArrayFunctionCall(resolvedCall) ||
                DescriptorUtils.getFqName(calleeDescriptor).asString() == "kotlin.arrayOfNulls"
    }
}
