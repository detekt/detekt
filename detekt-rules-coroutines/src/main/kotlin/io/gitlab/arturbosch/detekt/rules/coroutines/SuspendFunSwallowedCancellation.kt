package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getParameterForArgument
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi
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
 * they must always be immediately rethrown in the same context.
 *
 * Using `runCatching` increases this risk of mis-handling cancellation. If you catch and don't rethrow all the
 * `CancellationException` immediately, your coroutines are not cancelled even if you cancel their `CoroutineScope`.
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
 *
 * suspend fun baz() {
 *     try {
 *         bar(1_000L)
 *     } catch (e: IllegalStateException) {
 *         // catches CancellationException implicitly, since IllegalStateException is a super-class. Should be explicit
 *     }
 * }
 *
 * suspend fun qux() {
 *     try {
 *         bar(1_000L)
 *     } catch (e: CancellationException) {
 *         doSomeWork() // potentially long-running bit of work before propagating the cancellation
 *         throw e
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
 *     } catch (e: CancellationException) {
 *         throw e // explicitly caught and immediately re-thrown
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
@RequiresFullAnalysis
class SuspendFunSwallowedCancellation(config: Config) : Rule(
    config,
    description = "`CancellationException` must be specially handled and re-thrown when working with exceptions in a" +
        " suspending context. This includes `runCatching` as well as regular try-catch blocks."
) {

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

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)

        val function = expression.getParentOfType<KtFunction>(strict = true)
        val functionDescriptor = bindingContext[BindingContext.FUNCTION, function]
        if (functionDescriptor?.isSuspend != true) {
            // Don't care about the try-catch block unless it's in a suspending context
            return
        }

        for (catchClause in expression.catchClauses) {
            val parameter = catchClause?.catchParameter ?: continue
            if (parameter.isCancellationExceptionOrSuperClass()) {
                // This could be a CancellationException - we should make sure that it gets explicitly
                // re-thrown upwards immediately
                if (!catchClause.exceptionWasRethrown(parameter)) {
                    report(catchClause)
                } else if (catchClause.doesAnythingElseBeforeRethrowing()) {
                    // it does re-throw, but a potentially long-lasting bit of logic is called first. This is still a
                    // problem!
                    report(catchClause)
                }

                return // Only need to analyse the first CancellationException superclass instance
            }
        }
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

    private fun KtParameter.isCancellationExceptionOrSuperClass(): Boolean {
        val parameterFqName = bindingContext[BindingContext.VALUE_PARAMETER, this]
            ?.type
            ?.constructor
            ?.declarationDescriptor
            ?.fqNameOrNull()
            ?.asString()
        return parameterFqName in CANCELLATION_EXCEPTION_FQ_NAMES
    }

    /**
     * Checking for a [KtThrowExpression] which throws the same element as we received from the [KtCatchClause]. This
     * returns false if another exception with the same shadowed name as [cancellationException] is thrown.
     */
    private fun KtCatchClause.exceptionWasRethrown(cancellationException: KtParameter): Boolean {
        val thrownElements = catchBody
            ?.getChildrenOfType<KtThrowExpression>()
            .orEmpty()
            .asSequence()
            .map { expr -> expr.thrownExpression }
            .filterIsInstance<KtNameReferenceExpression>()
            .map { expr -> bindingContext[BindingContext.REFERENCE_TARGET, expr] }
            .filterIsInstance<DeclarationDescriptorWithSource>()
            .map { descriptor -> descriptor.source.getPsi() }
            .toList()

        // Returns false if thrownElements is empty, i.e. nothing was thrown
        return thrownElements.firstOrNull()?.textRange == cancellationException.textRange
    }

    private fun KtCatchClause.doesAnythingElseBeforeRethrowing(): Boolean {
        /**
         * We expect a minimum of two elements in this list:
         *  1) reference to the caught exception parameter
         *  2) throw expression where we throw it
         *
         * Anything before these means that some other work is being performed, which means the exception isn't being
         * immediately passed up the chain (and is therefore an issue). Anything afterwards won't be reached anyway
         * because we're throwing an exception, so it can be detected/handled by another rule.
         */
        val elements = catchBody?.collectDescendantsOfType<KtElement>().orEmpty()

        // Don't need to check the contents of the KtNameReferenceExpression, that's done as part of
        // exceptionWasRethrown(). It also verifies that something was thrown, which means we'll have a minimum of
        // two elements in the catch clause
        return elements[0] !is KtNameReferenceExpression || elements[1] !is KtThrowExpression
    }

    private fun report(expression: KtCallExpression) {
        report(
            CodeSmell(
                Entity.from((expression.calleeExpression as? PsiElement) ?: expression),
                "The `runCatching` has suspend call inside. You should either use specific `try-catch` " +
                    "only catching exception that you are expecting or rethrow the `CancellationException` if " +
                    "already caught."
            )
        )
    }

    private fun report(catchClause: KtCatchClause) {
        report(
            CodeSmell(
                entity = Entity.from(catchClause),
                message = "You should always catch and re-throw CancellationExceptions in" +
                    " a try block from a suspending function. The exception should be re-thrown" +
                    " immediately after catching it - it's intended to completely kill any" +
                    " running jobs in your coroutine.",
            )
        )
    }

    companion object {
        private val RUN_CATCHING_FQ = FqName("kotlin.runCatching")

        // Pulled from https://github.com/search?q=repo%3AKotlin%2Fkotlinx.coroutines+%22actual+typealias+CancellationException%22&type=code,
        // in descending order of priority.
        private val CANCELLATION_EXCEPTION_FQ_NAMES = listOf(
            "kotlinx.coroutines.CancellationException", // common typealias
            "kotlin.coroutines.cancellation.CancellationException", // native
            "java.util.concurrent.CancellationException", // JVM
            "java.lang.IllegalStateException", // JVM
            "java.lang.RuntimeException", // JVM
            "java.lang.Exception", // JVM
        )

        // Based on code from Kotlin project:
        // https://github.com/JetBrains/kotlin/commit/87bbac9d43e15557a2ff0dc3254fd41a9d5639e1
        private val COROUTINE_CONTEXT_FQ_NAME =
            COROUTINES_PACKAGE_FQ_NAME.child(KotlinName.identifier("coroutineContext"))
    }
}
