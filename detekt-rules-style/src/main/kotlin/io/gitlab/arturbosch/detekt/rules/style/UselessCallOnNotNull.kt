package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.error.ErrorType
import org.jetbrains.kotlin.types.isNullable

/*
 * Rule adapted from Kotlin's IntelliJ plugin:
 * https://github.com/JetBrains/kotlin/blob/f5d0a38629e7d2e7017ee645dc4d4bee60614e93/idea/src/org/jetbrains/kotlin/idea/inspections/collections/UselessCallOnNotNullInspection.kt
 */

/**
 * The Kotlin stdlib provides some functions that are designed to operate on references that may be null. These
 * functions can also be called on non-nullable references or on collections or sequences that are known to be empty -
 * the calls are redundant in this case and can be removed or should be changed to a call that does not check whether
 * the value is null or not.
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
 */
@ActiveByDefault(since = "1.2.0")
class UselessCallOnNotNull(config: Config) :
    Rule(
        config,
        "This call on a non-null reference may be reduced or removed. " +
            "Some calls are intended to be called on nullable collection or text types (e.g. `String?`)." +
            "When this call is used on a reference to a non-null type " +
            "(e.g. `String`) it is redundant and will have no effect, so it can be removed."
    ),
    RequiresTypeResolution {
    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        val safeExpression = expression as? KtSafeQualifiedExpression
        val notNullType = expression.receiverExpression.getType(bindingContext)?.isNullable() == false
        if (notNullType || safeExpression != null) {
            resolveCallForExpression(expression)
        }
    }

    private fun resolveCallForExpression(expression: KtQualifiedExpression) {
        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull() ?: return

        val conversion = uselessFqNames[fqName]
        if (conversion != null) {
            val shortName = fqName.shortName().asString()
            val message = if (conversion.replacementName == null) {
                "Remove redundant call to $shortName"
            } else {
                "Replace $shortName with ${conversion.replacementName}"
            }
            report(CodeSmell(Entity.from(expression), message))
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return

        val fqName = resolvedCall.resultingDescriptor.fqNameOrNull()
        if (fqName == listOfNotNull) {
            val varargs = resolvedCall.valueArguments.entries.single().value.arguments
            if (varargs.all { it.isNullable() == false }) {
                report(CodeSmell(Entity.from(expression), "Replace listOfNotNull with listOf"))
            }
        }
    }

    /**
     * Determines whether this [ValueArgument] is nullable, returning null if its type cannot be
     * determined.
     */
    private fun ValueArgument.isNullable(): Boolean? {
        val wrapperType = getArgumentExpression()?.getType(bindingContext) ?: return null
        val type = if (getSpreadElement() != null) {
            // in case of a spread operator (`*list`),
            // we actually want to get the generic parameter from the collection
            wrapperType.arguments.first().type
        } else {
            wrapperType
        }

        return type
            .takeUnless { it is ErrorType }
            ?.isNullable()
    }

    private data class Conversion(val replacementName: String? = null)

    companion object {
        private val uselessFqNames = mapOf(
            FqName("kotlin.collections.orEmpty") to Conversion(),
            FqName("kotlin.sequences.orEmpty") to Conversion(),
            FqName("kotlin.text.orEmpty") to Conversion(),
            FqName("kotlin.text.isNullOrEmpty") to Conversion("isEmpty"),
            FqName("kotlin.text.isNullOrBlank") to Conversion("isBlank"),
            FqName("kotlin.collections.isNullOrEmpty") to Conversion("isEmpty")
        )

        private val listOfNotNull = FqName("kotlin.collections.listOfNotNull")
    }
}
