package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtClassInitializer
import org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypes
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.sam.SamConstructorDescriptor
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
@Suppress("TooManyFunctions")
class MissingUseCall(config: Config = Config.empty) :
    Rule(
        config,
        "Usage of `Closeable` detected without `use` call. Using `Closeable` without `use` " +
            "can be problematic as closing `Closeable` may throw exception.",
    ),
    RequiresTypeResolution {
    private val traversedParentExpression: MutableSet<PsiElement> = mutableSetOf()
    private val usedReferences: MutableSet<CallableDescriptor> = mutableSetOf()

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
                    Entity.from(expression),
                    "${
                        (
                            expression.findDescendantOfType<KtNameReferenceExpression>()
                                ?: expression
                            ).text
                    } doesn't call `use` to access the `Closeable`"
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
        val expressionParent = getParentChainExpression(expression) ?: return false
        return when {
            expressionParent is KtQualifiedExpression -> {
                val expressionCallDescriptor =
                    expression.getResolvedCall(bindingContext)?.resultingDescriptor
                // this should not first chain as that can't be helper chain
                if (
                    expression != expressionParent.firstCallableReceiverOrNull() &&
                    expressionCallDescriptor !is ConstructorDescriptor &&
                    expressionCallDescriptor !is SamConstructorDescriptor
                ) {
                    // probably some helper method which takes and return closeable
                    return false
                }
                expressionParent.doesEndWithUse().not() &&
                    expressionParent.firstCallableReceiverOrNull().isCloseableNotUsed()
            }

            isPartOfIfElseExpressionReturningCloseable(expression) -> {
                isExpressionUsedOnSameOrNextLine(expression).not()
            }

            isParentFunctionReturnsCloseable(expression) -> {
                false
            }

            isParamForClosableOrFunReturningClosable(expression) -> {
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

    private fun isParamForClosableOrFunReturningClosable(expression: KtExpression): Boolean {
        if (expression.parent !is KtValueArgument) return false
        val callExpression = expression.parent.parent.parent as? KtCallExpression ?: return false
        val descriptor =
            callExpression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return false
        val returnType = descriptor.returnType ?: return false
        return isChildOfCloseable(returnType)
    }

    private fun KtQualifiedExpression.doesEndWithUse(): Boolean {
        receiverExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.let {
            usedReferences.add(it)
        }
        return selectorExpression
            .getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull() in useFqNames
    }

    private fun KtElement?.isCloseableNotUsed(): Boolean {
        this ?: return true
        return usedReferences.contains(this.getResolvedCall(bindingContext)?.resultingDescriptor)
    }

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
                    it.parentsWithSelf
                        .firstOrNull { element -> element !is KtParenthesizedExpression } as? KtQualifiedExpression
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
        val expressionAfterParens = expression.parents.firstOrNull { it !is KtParenthesizedExpression } ?: return false
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

    private fun getParentChainExpression(closeableExpression: KtExpression): PsiElement? {
        var expression: PsiElement? = closeableExpression
        while (
            expression?.parent is KtQualifiedExpression ||
            (
                expression?.parent is KtPostfixExpression &&
                    (expression.parent as KtPostfixExpression).operationToken == KtTokens.EXCLEXCL
                )
        ) {
            expression = if (expression.parent is KtPostfixExpression) {
                expression.parent.parent
            } else {
                expression.parent
            }
        }
        return expression
    }

    private fun KtQualifiedExpression.firstCallableReceiverOrNull(): KtElement? {
        fun KtExpression.isCallableExpression() =
            this.getResolvedCall(bindingContext)?.resultingDescriptor is FunctionDescriptor

        var expression = receiverExpression

        if (expression.isCallableExpression().not()) return null

        while (
            expression is KtQualifiedExpression &&
            isCallableExpression()
        ) {
            expression = expression.receiverExpression
        }
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
