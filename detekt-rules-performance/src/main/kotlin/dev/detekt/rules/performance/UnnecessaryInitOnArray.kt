package dev.detekt.rules.performance

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression

/**
 * Flags array constructors that use lambda builders returning the same default value when
 * the default initialization would produce the same result.
 *
 * Using lambda builders like `IntArray(10) { 0 }` creates unnecessary overhead compared to
 * `IntArray(10)` when the lambda returns the default value. The lambda version creates the array
 * and then runs a loop to set each element to the same default value, while the constructor
 * without lambda directly creates an array with default values.
 *
 * <noncompliant>
 * val a = IntArray(10) { 0 }
 * val b = FloatArray(10) { 0F }
 * val c = LongArray(10) { 0L }
 * val d = BooleanArray(10) { false }
 * val e = CharArray(10) { '\u0000' }
 * </noncompliant>
 *
 * <compliant>
 * val a = IntArray(10)
 * val b = FloatArray(10)
 * val c = LongArray(10)
 * val d = BooleanArray(10)
 * val e = CharArray(10)
 *
 * // with side effect(s)
 * val k = IntArray(10) {
 *     println("Some side-effect")
 *     0
 * }
 * </compliant>
 */
class UnnecessaryInitOnArray(config: Config) :
    Rule(
        config,
        "Flags array constructors that use lambda builders returning the same default value when the " +
            "default initialization would produce the same result."
    ) {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val arrayClassName = getArrayClassNameOrNull(expression) ?: return

        val lambdaArgument = expression.valueArguments
            .mapNotNull { it.getArgumentExpression() }
            .filterIsInstance<KtLambdaExpression>()
            .firstOrNull()
            ?: return

        if (hasSideEffects(lambdaArgument)) return

        if (returnsDefaultValue(lambdaArgument, arrayClassName)) {
            val message = "Unnecessary lambda builder returning default value. Use constructor without lambda."
            report(Finding(Entity.from(expression), message))
        }
    }

    private fun getArrayClassNameOrNull(expression: KtCallExpression): String? {
        val className = expression.getCallNameExpression()?.getReferencedName()
        return if (className in arrayTypeDefaultPatterns.keys) {
            className
        } else {
            null
        }
    }

    private fun hasSideEffects(lambda: KtLambdaExpression): Boolean {
        val body = lambda.bodyExpression ?: return false

        val statements = body.statements
        return statements.size > 1
    }

    private fun returnsDefaultValue(lambda: KtLambdaExpression, arrayClassName: String): Boolean {
        val body = lambda.bodyExpression ?: return false
        val returnExpression = body.statements.lastOrNull() ?: body

        val defaultPatterns = arrayTypeDefaultPatterns[arrayClassName] ?: return false
        return defaultPatterns.contains(returnExpression.text.trim())
    }

    companion object {
        private val arrayTypeDefaultPatterns: Map<String, Set<String>> = mapOf(
            "IntArray" to setOf("0", "0.toInt()"),
            "UIntArray" to setOf("0u", "0.toUInt()"),
            "FloatArray" to setOf("0F", "0.0F", "0f", "0.0f", "0.toFloat()"),
            "LongArray" to setOf("0L", "0l", "0.toLong()"),
            "ULongArray" to setOf("0uL", "0.toULong()"),
            "ShortArray" to setOf("0.toShort()", "0"),
            "UShortArray" to setOf("0.toUShort()", "0"),
            "BooleanArray" to setOf("false"),
            "ByteArray" to setOf("0.toByte()", "0"),
            "UByteArray" to setOf("0.toUByte()", "0u"),
            "DoubleArray" to setOf("0.0", "0.toDouble()"),
            "CharArray" to setOf("'\\u0000'", "'\u0000'", "0.toChar()")
        )
    }
}
