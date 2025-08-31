package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaTypeNullability
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_COLLECTIONS_PACKAGE
import org.jetbrains.kotlin.name.StandardClassIds.BASE_SEQUENCES_PACKAGE
import org.jetbrains.kotlin.name.StandardClassIds.BASE_TEXT_PACKAGE
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny

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
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        val calleeText = expression.getCalleeExpressionIfAny()?.text
        if (calleeText !in uselessCallNames) return

        analyze(expression) {
            if (expression.receiverExpression.expressionType?.nullability != KaTypeNullability.NON_NULLABLE) return

            val callableId = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId ?: return
            val conversion = uselessCallFqNames[callableId] ?: return

            val message = if (conversion.replacementName != null) {
                "Replace $calleeText with ${conversion.replacementName}"
            } else {
                "Remove redundant call to $calleeText"
            }
            report(Finding(Entity.from(expression), message))
        }
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeText = expression.calleeExpression?.text ?: return
        if (calleeText !in ofNotNullNames) return

        analyze(expression) {
            val callableId = expression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId

            val replacementName = ofNotNullFqNames[callableId]?.replacementName ?: return

            val hasNullableType = expression.valueArguments.any { valueArgument ->
                val type = valueArgument.getArgumentExpression()?.expressionType?.let {
                    if (valueArgument.getSpreadElement() != null) {
                        (it as? KaClassType)?.typeArguments?.firstOrNull()?.type
                    } else {
                        it
                    }
                }
                type?.nullability != KaTypeNullability.NON_NULLABLE
            }
            if (!hasNullableType) {
                report(Finding(Entity.from(expression), "Replace $calleeText with $replacementName"))
            }
        }
    }

    private data class Conversion(val replacementName: String? = null)

    companion object {
        private val uselessCallFqNames = mapOf(
            callableId(BASE_COLLECTIONS_PACKAGE, "orEmpty") to Conversion(),
            callableId(BASE_SEQUENCES_PACKAGE, "orEmpty") to Conversion(),
            callableId(BASE_TEXT_PACKAGE, "orEmpty") to Conversion(),
            callableId(BASE_TEXT_PACKAGE, "isNullOrEmpty") to Conversion("isEmpty"),
            callableId(BASE_TEXT_PACKAGE, "isNullOrBlank") to Conversion("isBlank"),
            callableId(BASE_COLLECTIONS_PACKAGE, "isNullOrEmpty") to Conversion("isEmpty")
        )

        private val uselessCallNames = uselessCallFqNames.keys.map { it.callableName.identifier }

        private val ofNotNullFqNames = mapOf(
            callableId(BASE_COLLECTIONS_PACKAGE, "listOfNotNull") to Conversion("listOf"),
            callableId(BASE_COLLECTIONS_PACKAGE, "setOfNotNull") to Conversion("setOf"),
        )

        private val ofNotNullNames = ofNotNullFqNames.keys.map { it.callableName.asString() }

        private fun callableId(pkg: FqName, callable: String) = CallableId(pkg, Name.identifier(callable))
    }
}
