package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.TypeUtils

/**
 * The Kotlin stdlib provides some functions that are designed to operate on references that may be null. These
 * functions can also be called on non-nullable references or on collections or sequences that are known to be empty -
 * the calls are redundant in this case and can be removed or should be changed to a call that does not check whether
 * the value is null or not.
 *
 * Rule adapted from Kotlin's IntelliJ plugin: https://github.com/JetBrains/kotlin/blob/f5d0a38629e7d2e7017ee645dc4d4bee60614e93/idea/src/org/jetbrains/kotlin/idea/inspections/collections/UselessCallOnNotNullInspection.kt
 *
 * <noncompliant>
 * val testList = listOf("string").orEmpty()
 * val testList2 = listOf("string").orEmpty().map { _ }
 * val testString = ""?.isNullOrBlank()
 * </noncompliant>
 *
 * <compliant>
 * val testList = listOf("string")
 * val testList2 = listOf("string").map { }
 * val testString = ""?.isBlank()
 * </compliant>
 *
 * @active since v1.2.0
 */
class UselessCallOnNotNull(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "UselessCallOnNotNull",
        Severity.Performance,
        "This call on non-null reference may be reduced or removed. Some calls are intended to be called on nullable " +
                "collection or text types (e.g. String?). When this call is used on a reference to a non-null type " +
                "(e.g. String) it is redundant and will have no effect, so it can be removed.",
        Debt.FIVE_MINS
    )

    private val uselessFqNames = mapOf(
        "kotlin.collections.orEmpty" to Conversion(),
        "kotlin.sequences.orEmpty" to Conversion(),
        "kotlin.text.orEmpty" to Conversion(),
        "kotlin.text.isNullOrEmpty" to Conversion("isEmpty"),
        "kotlin.text.isNullOrBlank" to Conversion("isBlank")
    )

    private val uselessNames = toShortFqNames(uselessFqNames.keys)

    @Suppress("ReturnCount")
    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return
        val selector = expression.selectorExpression as? KtCallExpression ?: return
        val calleeExpression = selector.calleeExpression ?: return
        if (calleeExpression.text !in uselessNames) return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        if (uselessFqNames.contains(resolvedCall.resultingDescriptor.fqNameOrNull()?.asString())) {
            val safeExpression = expression as? KtSafeQualifiedExpression
            val notNullType =
                expression.receiverExpression.getType(bindingContext)?.let { TypeUtils.isNullableType(it) } == false
            if (notNullType || safeExpression != null) {
                report(CodeSmell(issue, Entity.from(expression), ""))
            }
        }
    }

    private companion object {
        data class Conversion(val replacementName: String? = null)

        private fun toShortFqNames(longNames: Set<String>): Set<String> {
            return longNames.mapTo(mutableSetOf()) { fqName -> fqName.takeLastWhile { it != '.' } }
        }
    }
}
