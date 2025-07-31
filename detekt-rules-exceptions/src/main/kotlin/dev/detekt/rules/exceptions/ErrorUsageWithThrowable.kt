package dev.detekt.rules.exceptions

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * This rule reports usage of `error` method with [Throwable] parameter, i.e, `error(throwable)`.
 * The above will result in `throw IllegalStateException(throwable.toString())` which doesn't
 * provide any info about `throwable.message` or `stackTrace`.
 * Instead, use `throw IllegalStateException(throwable)` to rethrow the throwable as `IllegalStateException`.
 *
 * <noncompliant>
 * fun foo() {
 *     try {
 *         // ... some code
 *     } catch(e: IOException) {
 *         // some addition handling
 *         error(e)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() {
 *     try {
 *         // ... some code
 *     } catch(e: IOException) {
 *         // some addition handling
 *         throw e // or throw IllegalStateException(<some custom error msg>, e)
 *     }
 * }
 * </compliant>
 */
class ErrorUsageWithThrowable(config: Config) :
    Rule(
        config,
        "Passing `Throwable` in `error` method is ambiguous. Use " +
            "`error(throwable.message ?: \"No error message provided\")` instead."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (!expression.isCalling(ERROR_CALLABLE_ID)) return
        val errorMsgValueArg = expression.valueArguments.getOrNull(0)?.getArgumentExpression() ?: return
        if (errorMsgValueArg.isThrowableSubtypeOfThrowable()) {
            report(Finding(Entity.from(errorMsgValueArg), description))
        }
    }

    private fun KtExpression.isThrowableSubtypeOfThrowable(): Boolean = analyze(this) {
        val expressionType = expressionType ?: return false
        sequence {
            yield(expressionType)
            yieldAll(expressionType.allSupertypes)
        }
            .any { type -> throwableTypes.any { type.isClassType(it) } }
    }

    companion object {
        private val ERROR_CALLABLE_ID = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("error"))
        private val throwableTypes = setOf(
            ClassId.fromString("kotlin/Throwable"),
            ClassId.fromString("java/lang/Throwable")
        )
    }
}
