package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.js.translate.callTranslator.getReturnType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Prefer using `use` function with `Closeable` or `AutoCloseable`. As `use` function closes it resource correctly
 * whether an exception is thrown or not.
 *
 * <noncompliant>
 * val myCloseable = MyCloseable()
 * // do stuff with myCloseable
 *
 * MyClosable().doStuff()
 *
 * functionThatReturnsClosable().doStuff()
 *
 * </noncompliant>
 *
 * <compliant>
 * MyCloseable().use {
 *     // do stuff with myCloseable
 * }
 *
 * MyClosable().use { it.doStuff() }
 *
 * functionThatReturnsClosable().use { it.doStuff() }
 * </compliant>
 */
@RequiresTypeResolution
class MissingUseCall(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        Severity.Warning,
        "Usage of `Closeable` detected with `use` call. Using `Closeable` without `use` can be problematic " +
            "as closing `Closeable` may throw exception.",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val calleeReturnType = expression.getResolvedCall(bindingContext)?.getReturnType() ?: return
        val isCloseable = isChildOfAutoCloseable(calleeReturnType)
        if (isCloseable.not()) return
        if (shouldReport(expression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "${expression.text} doesn't call `use` to access the `Closeable`"
                )
            )
        }
    }

    private fun isChildOfAutoCloseable(calleeReturnType: KotlinType): Boolean {
        val isCloseable = calleeReturnType.supertypes()
            .map {
                it.fqNameOrNull()
            }
            .any {
                it in listOfCloseables
            }
        return isCloseable
    }

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)
        val expressionType = expression.getType(bindingContext) ?: return
        val isCloseable = isChildOfAutoCloseable(expressionType)
        if (isCloseable.not()) return
        if (shouldReport(expression)) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "${expression.text} doesn't call `use` to access the `Closeable`"
                )
            )
        }
    }

    private fun shouldReport(expression: KtExpression): Boolean {
        val expressionParent = getParentExpression(expression)
        return if (expressionParent !is KtDotQualifiedExpression) {
            true
        } else {
            expressionParent
                .selectorExpression
                .getResolvedCall(bindingContext)
                ?.resultingDescriptor
                ?.fqNameOrNull() !in useFqNames
        }
    }

    private fun getParentExpression(closeableExpression: KtExpression): PsiElement? {
        var expression: PsiElement? = closeableExpression
        do {
            expression = expression?.parent
        } while (expression is KtDotQualifiedExpression && expression.selectorExpression == closeableExpression)
        return expression
    }

    companion object {
        private val listOfCloseables = listOf(
            FqName("java.lang.AutoCloseable"),
            FqName("java.io.Closeable"),
        )

        private val useFqNames = listOf(
            FqName("kotlin.io.use"),
            FqName("kotlin.use"),
        )
    }
}
