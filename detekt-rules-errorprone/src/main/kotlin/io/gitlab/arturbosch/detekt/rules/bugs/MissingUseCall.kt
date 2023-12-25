package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.getParentExpressionAfterParenthesis
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypes
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Prefer using the `use` function with `Closeable` or `AutoCloseable`. As `use` function ensures proper closure of
 * `Closable`. It also properly handles exceptions if raised while closing the resource
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
@Suppress("TooManyFunctions")
class MissingUseCall(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        javaClass.simpleName,
        "Usage of `Closeable` detected without `use` call. Using `Closeable` without `use` can be problematic " +
            "as closing `Closeable` may throw exception.",
    )

    private val traversedParentExpression: MutableList<PsiElement> = mutableListOf()


    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkAndReport(expression)
    }

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)
        checkAndReport(expression)
    }

    private fun checkAndReport(expression: KtExpression) {
        val isCloseable = isChildOfCloseable(expression)
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

    private fun isChildOfCloseable(expression: KtExpression): Boolean {
        val expressionType = expression.getType(bindingContext) ?: return false
        return isChildOfCloseable(expressionType)
    }

    private fun isChildOfCloseable(type: KotlinType): Boolean {
        val isCloseable = type.supertypes()
            .map {
                it.fqNameOrNull()
            }
            .any {
                it in listOfCloseables
            }
        return isCloseable
    }

    private fun shouldReport(expression: KtExpression): Boolean {
        val expressionParent = getParentExpression(expression) ?: return false
        return when {
            expressionParent is KtQualifiedExpression -> {
                expressionParent.doesEndWithUse().not()
            }

            isPartOfIfElseExpressionReturningCloseable(expression) -> {
                isExpressionUsedOnSameOrNextLine(expression).not()
            }

            isParentFunctionReturnsCloseable(expression) -> {
                false
            }

            expressionParent is KtProperty -> {
                // rhs has already been analysed
                traversedParentExpression.contains(expressionParent.children.getOrNull(0)).not()
            }

            else -> {
                true
            }
        }.also { traversedParentExpression.add(expressionParent) }
    }

    private fun isParentFunctionReturnsCloseable(expression: KtExpression): Boolean {
        val parent = expression.getParentOfType<KtNamedFunction>(
            true,
            KtLambdaExpression::class.java,
            KtClassInitializer::class.java,
            KtClassBody::class.java,
        ) ?: return false
        val functionReturnType =
            bindingContext[BindingContext.FUNCTION, parent]?.returnType ?: return false
        return isChildOfCloseable(functionReturnType)
    }

    private fun KtQualifiedExpression.doesEndWithUse() = selectorExpression
        .getResolvedCall(bindingContext)
        ?.resultingDescriptor
        ?.fqNameOrNull() in useFqNames

    private fun isExpressionUsedOnSameOrNextLine(expression: KtExpression): Boolean {
        val parent = expression.getParentOfTypes(
            true,
            KtQualifiedExpression::class.java,
            KtProperty::class.java
        )

        return when (parent) {
            is KtQualifiedExpression -> {
                parent.doesEndWithUse()
            }

            is KtProperty -> {
                parent.siblings(forward = true, withItself = false).filter {
                    it.text.isNotBlank()
                }.mapNotNull {
                    it.getParentExpressionAfterParenthesis(false) as? KtQualifiedExpression
                }.filter {
                    it.doesEndWithUse()
                }.any {
                    it.receiverExpression.text == parent.name
                }
            }

            else -> {
                false
            }
        }
    }

    @Suppress("ReturnCount")
    private fun isPartOfIfElseExpressionReturningCloseable(expression: KtExpression): Boolean {
        val expressionAfterParens = expression.getParentExpressionAfterParenthesis() ?: return false
        val (ifExpression, containerExpression) =
            @Suppress("BracesOnIfStatements")
            if (expressionAfterParens is KtContainerNodeForControlStructureBody) {
                expressionAfterParens.parent to expressionAfterParens.expression
            } else if (expressionAfterParens.parent is KtContainerNodeForControlStructureBody) {
                expressionAfterParens.parent.parent to
                    (expressionAfterParens.parent as KtContainerNodeForControlStructureBody).expression
            } else {
                null
            } ?: return false
        if (ifExpression !is KtIfExpression) return false
        val containerExpressionLastExpression =
            if (containerExpression is KtBlockExpression) {
                containerExpression.statements.lastOrNull()
            } else {
                containerExpression
            }
        containerExpressionLastExpression ?: return false
        return isChildOfCloseable(containerExpressionLastExpression)
    }

    @Suppress("ComplexCondition")
    private fun getParentExpression(closeableExpression: KtExpression): PsiElement? {
        var expression: PsiElement? = closeableExpression
        do {
            expression = expression?.parent
        } while (
            (
                expression is KtQualifiedExpression &&
                    expression.selectorExpression == closeableExpression
                ) ||
            (expression is KtPostfixExpression && expression.operationToken == KtTokens.EXCLEXCL)
        )
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
