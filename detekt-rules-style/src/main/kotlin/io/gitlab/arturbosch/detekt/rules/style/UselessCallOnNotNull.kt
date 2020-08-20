package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.isNullable

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
 * val testList3 = listOfNotNull("string")
 * val testString = ""?.isNullOrBlank()
 * </noncompliant>
 *
 * <compliant>
 * val testList = listOf("string")
 * val testList2 = listOf("string").map { }
 * val testList3 = listOf("string")
 * val testString = ""?.isBlank()
 * </compliant>
 *
 * @active since v1.2.0
 * @requiresTypeResolution
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
        FqName("kotlin.collections.orEmpty") to Conversion(),
        FqName("kotlin.sequences.orEmpty") to Conversion(),
        FqName("kotlin.text.orEmpty") to Conversion(),
        FqName("kotlin.text.isNullOrEmpty") to Conversion("isEmpty"),
        FqName("kotlin.text.isNullOrBlank") to Conversion("isBlank")
    )

    @Suppress("ReturnCount")
    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull() ?: return
        val conversion = uselessFqNames[fqName] ?: return

        val safeExpression = expression as? KtSafeQualifiedExpression
        val notNullType = expression.receiverExpression.getType(bindingContext)?.isNullable() == false
        if (notNullType || safeExpression != null) {
            val shortName = fqName.shortName().asString()
            val message = if (conversion.replacementName == null) {
                "Remove redundant call to $shortName"
            } else {
                "Replace $shortName with ${conversion.replacementName}"
            }
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull() ?: return
        if (fqName != FqName("kotlin.collections.listOfNotNull")) return

        val varargs = resolvedCall.valueArguments.entries.single().value.arguments
        if (varargs.none { it.isNullable() }) {
            report(CodeSmell(issue, Entity.from(expression), "Replace listOfNotNull with listOf"))
        }
    }

    private fun ValueArgument.isNullable(): Boolean =
        getArgumentExpression()?.getType(bindingContext)?.isNullable() == true

    private data class Conversion(val replacementName: String? = null)
}
