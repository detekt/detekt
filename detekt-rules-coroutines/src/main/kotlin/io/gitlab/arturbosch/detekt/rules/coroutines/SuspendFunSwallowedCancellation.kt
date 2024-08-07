package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import org.jetbrains.kotlin.name.Name as KotlinName

/**
 * `suspend` functions should not be called inside `runCatching`'s lambda block, because `runCatching` catches all the
 * `Exception`s. For Coroutines to work in all cases, developers should make sure to propagate `CancellationException`
 * exceptions. This means `CancellationException` should never be:
 * * caught and swallowed (even if logged)
 * * caught and propagated to external systems
 * * caught and shown to the user
 *
 * they must always be rethrown in the same context.
 *
 * Using `runCatching` increases this risk of mis-handling cancellation. If you catch and don't rethrow all the
 * `CancellationException`, your coroutines are not cancelled even if you cancel their `CoroutineScope`.
 *
 * This can very easily lead to:
 * * unexpected crashes
 * * extremely hard to diagnose bugs
 * * memory leaks
 * * performance issues
 * * battery drain
 *
 * See reference, [Kotlin doc](https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative).
 *
 * If your project wants to use `runCatching` and `Result` objects, it is recommended to write a `coRunCatching`
 * utility function which immediately re-throws `CancellationException`; and forbid `runCatching` and `suspend`
 * combinations by activating this rule.
 *
 * <noncompliant>
 * @@Throws(IllegalStateException::class)
 * suspend fun bar(delay: Long) {
 *     check(delay <= 1_000L)
 *     delay(delay)
 * }
 *
 * suspend fun foo() {
 *     runCatching {
 *         bar(1_000L)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * @@Throws(IllegalStateException::class)
 * suspend fun bar(delay: Long) {
 *     check(delay <= 1_000L)
 *     delay(delay)
 * }
 *
 * suspend fun foo() {
 *     try {
 *         bar(1_000L)
 *     } catch (e: IllegalStateException) {
 *         // handle error
 *     }
 * }
 *
 * // Alternate
 * @@Throws(IllegalStateException::class)
 * suspend fun foo() {
 *     bar(1_000L)
 * }
 * </compliant>
 *
 */
class SuspendFunSwallowedCancellation(config: Config) :
    Rule(
        config,
        "`runCatching` does not propagate `CancellationException`, don't use it with `suspend` lambda blocks."
    ),
    RequiresTypeResolution {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val resultingDescriptor = expression.getResolvedCall(bindingContext)?.resultingDescriptor ?: return

        if (resultingDescriptor.fqNameSafe != RUN_CATCHING_FQ) return

        fun shouldTraverseInside(element: PsiElement): Boolean =
            expression == element || shouldTraverseInside(element, bindingContext)

        expression.anyDescendantOfType<KtExpression>(::shouldTraverseInside) { descendant ->
            descendant.hasSuspendCalls()
        }.ifTrue { report(expression) }
    }

    @Suppress("ReturnCount")
    private fun shouldTraverseInside(psiElement: PsiElement, bindingContext: BindingContext): Boolean {
        return when (psiElement) {
            is KtCallExpression -> {
                val callableDescriptor =
                    (psiElement.getResolvedCall(bindingContext)?.resultingDescriptor as? FunctionDescriptor)
                        ?: return false

                callableDescriptor.fqNameSafe != RUN_CATCHING_FQ && callableDescriptor.isInline
            }

            is KtValueArgument -> {
                val callExpression = psiElement.getParentOfType<KtCallExpression>(true)
                val valueParameterDescriptor =
                    callExpression?.getResolvedCall(bindingContext)?.getParameterForArgument(psiElement) ?: return false

                valueParameterDescriptor.isCrossinline.not() && valueParameterDescriptor.isNoinline.not()
            }

            else -> true
        }
    }

    @Suppress("ReturnCount")
    private fun KtExpression.hasSuspendCalls(): Boolean {
        return when (this) {
            is KtForExpression -> {
                val loopRangeIterator = bindingContext[BindingContext.LOOP_RANGE_ITERATOR_RESOLVED_CALL, loopRange]
                val loopRangeHasNext =
                    bindingContext[BindingContext.LOOP_RANGE_HAS_NEXT_RESOLVED_CALL, loopRange]
                val loopRangeNext = bindingContext[BindingContext.LOOP_RANGE_NEXT_RESOLVED_CALL, loopRange]
                listOf(loopRangeIterator, loopRangeHasNext, loopRangeNext).any {
                    it?.resultingDescriptor?.isSuspend == true
                }
            }

            is KtCallExpression, is KtOperationExpression -> {
                val resolvedCall = getResolvedCall(bindingContext) ?: return false
                (resolvedCall.resultingDescriptor as? FunctionDescriptor)?.isSuspend == true
            }

            is KtNameReferenceExpression -> {
                val resolvedCall = getResolvedCall(bindingContext) ?: return false
                val propertyDescriptor = resolvedCall.resultingDescriptor as? PropertyDescriptor
                propertyDescriptor?.fqNameSafe == COROUTINE_CONTEXT_FQ_NAME
            }

            else -> {
                false
            }
        }
    }

    private fun report(
        expression: KtCallExpression,
    ) {
        report(
            CodeSmell(
                Entity.from((expression.calleeExpression as? PsiElement) ?: expression),
                "The `runCatching` has suspend call inside. You should either use specific `try-catch` " +
                    "only catching exception that you are expecting or rethrow the `CancellationException` if " +
                    "already caught."
            )
        )
    }

    companion object {
        private val RUN_CATCHING_FQ = FqName("kotlin.runCatching")

        // Based on code from Kotlin project:
        // https://github.com/JetBrains/kotlin/commit/87bbac9d43e15557a2ff0dc3254fd41a9d5639e1
        private val COROUTINE_CONTEXT_FQ_NAME =
            COROUTINES_PACKAGE_FQ_NAME.child(KotlinName.identifier("coroutineContext"))
    }
}
