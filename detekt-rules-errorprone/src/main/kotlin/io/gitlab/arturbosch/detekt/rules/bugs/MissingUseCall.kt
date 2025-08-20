package io.gitlab.arturbosch.detekt.rules.bugs

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
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
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfTypes
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
import org.jetbrains.kotlin.psi.psiUtil.siblings

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
    RequiresAnalysisApi {

    private val traversedParentExpression: MutableSet<PsiElement> = mutableSetOf()
    private val usedReferences: MutableSet<KaSymbol> = mutableSetOf()

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        checkAndReport(expression)
    }

    override fun visitObjectLiteralExpression(expression: KtObjectLiteralExpression) {
        super.visitObjectLiteralExpression(expression)
        checkAndReport(expression)
    }

    private fun checkAndReport(expression: KtExpression) {
        analyze(expression) {
            val isCloseable = isChildOfCloseable(expression)
            if (isCloseable.not()) return
            if (shouldReport(expression)) {
                report(
                    Finding(
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
    }

    private fun KaSession.isChildOfCloseable(expr: KtExpression): Boolean {
        val symbol = if (expr is KtObjectLiteralExpression) {
            expr.symbol
        } else {
            KtPsiUtil.safeDeparenthesize(expr).resolveToCall()?.singleFunctionCallOrNull()?.symbol?.returnType?.symbol
        } ?: return false
        return isChildOfCloseable(symbol)
    }

    private fun KaSession.isChildOfCloseable(symbol: KaSymbol): Boolean {
        val superTypes = (symbol as? KaClassSymbol)?.superTypes.orEmpty().flatMap { listOf(it) + it.allSupertypes }
        return superTypes.any { it.symbol?.classId?.asSingleFqName() in listOfCloseables }
    }

    private fun KaSession.shouldReport(expression: KtExpression): Boolean {
        val expressionParent = getParentChainExpression(expression) ?: return false
        return when {
            expressionParent is KtQualifiedExpression -> {
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

    private fun KaSession.isParentFunctionReturnsCloseable(expression: KtExpression): Boolean {
        val parent = expression.getParentOfType<KtNamedFunction>(
            true,
            KtLambdaExpression::class.java,
            KtClassInitializer::class.java,
            KtClassBody::class.java,
        ) ?: return false
        val symbol = parent.returnType.symbol ?: return false
        return isChildOfCloseable(symbol)
    }

    private fun KaSession.isParamForClosableOrFunReturningClosable(expression: KtExpression): Boolean {
        if (expression.parent !is KtValueArgument) return false
        val callExpression = expression.parent.parent.parent as? KtCallExpression ?: return false
        val symbol =
            callExpression.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.returnType?.symbol ?: return false
        return isChildOfCloseable(symbol)
    }

    context(session: KaSession)
    private fun KtQualifiedExpression.doesEndWithUse(): Boolean = with(session) {
        receiverExpression.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()?.symbol?.let {
            usedReferences.add(it)
        }
        selectorExpression?.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId
            ?.asSingleFqName() in useFqNames
    }

    context(session: KaSession)
    private fun KtElement?.isCloseableNotUsed(): Boolean {
        this ?: return true
        return with(session) {
            resolveToCall()?.singleFunctionCallOrNull()?.symbol as? KaSymbol in usedReferences
        }
    }

    private fun KaSession.isExpressionUsedOnSameOrNextLine(expression: KtExpression): Boolean {
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
    private fun KaSession.isPartOfIfElseExpressionReturningCloseable(expression: KtExpression): Boolean {
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

    context(session: KaSession)
    private fun KtQualifiedExpression.firstCallableReceiverOrNull(): KtElement? {
        fun KtExpression.isCallableExpression(): Boolean = with(session) {
            resolveToCall()?.singleFunctionCallOrNull() != null
        }

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
