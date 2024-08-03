package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

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
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val descriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return
        if (descriptor.fqNameOrNull()?.toString() != ERROR_FQ_NAME) return
        val errorMsgValueArg = expression.valueArguments.getOrNull(0) ?: return
        val errorMsgTypeInfo = bindingContext[
            BindingContext.EXPRESSION_TYPE_INFO,
            errorMsgValueArg.getArgumentExpression()
        ]?.type ?: return
        if (errorMsgTypeInfo.isThrowableSubtypeOfThrowable()) {
            report(CodeSmell(Entity.from(errorMsgValueArg), description))
        }
    }

    private fun KotlinType.isThrowableSubtypeOfThrowable(): Boolean =
        this.fqNameOrNull() in throwableTypes ||
            this.supertypes()
                .any { it.fqNameOrNull() in throwableTypes }

    companion object {
        private const val ERROR_FQ_NAME = "kotlin.error"
        private val throwableTypes = setOf(
            StandardNames.FqNames.throwable,
            FqName("java.lang.Throwable")
        )
    }
}
