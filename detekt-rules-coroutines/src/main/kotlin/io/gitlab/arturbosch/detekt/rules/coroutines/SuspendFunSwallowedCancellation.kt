package io.gitlab.arturbosch.detekt.rules.coroutines

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.KaCompoundVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.builtins.StandardNames.COROUTINES_PACKAGE_FQ_NAME
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtThrowExpression
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

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
class SuspendFunSwallowedCancellation(config: Config) :
    Rule(
        config,
        description = "`CancellationException` must be specially handled and re-thrown when working with exceptions " +
            "in a suspending context. This includes `runCatching` as well as regular try-catch blocks."
    ),
    RequiresAnalysisApi {

    private val KtFunction.isSuspend: Boolean
        get() {
            return when (this) {
                is KtNamedFunction -> {
                    analyze(this) {
                        (symbol as? KaNamedFunctionSymbol)?.isSuspend == true
                    }
                }

                is KtFunctionLiteral -> {
                    analyze(this) {
                        val lambdaExpression = parent as? KtLambdaExpression ?: return false
                        val expectedType = lambdaExpression.expectedType ?: return false

                        return expectedType.isSuspendFunctionType
                    }
                }

                else -> false
            }
        }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val callableId = analyze(expression) {
            expression.resolveToCall()
                ?.successfulFunctionCallOrNull()
                ?.symbol
                ?.callableId
        }

        if (callableId != RUN_CATCHING_CALLABLE_ID) return

        fun shouldTraverseInside(element: PsiElement): Boolean =
            expression == element || shouldTraverseInsideImpl(element)

        expression.anyDescendantOfType<KtExpression>(::shouldTraverseInside) { descendant ->
            descendant.hasSuspendCalls()
        }.ifTrue { report(expression) }
    }

    override fun visitTryExpression(expression: KtTryExpression) {
        super.visitTryExpression(expression)

        val function = expression.getParentOfType<KtFunction>(strict = true)

        if (function?.isSuspend != true) {
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

    private fun shouldTraverseInsideImpl(element: PsiElement): Boolean = when (element) {
        is KtCallExpression -> {
            val functionSymbol = analyze(element) {
                element.resolveToCall()
                    ?.successfulFunctionCallOrNull()
                    ?.symbol
                    as? KaNamedFunctionSymbol
            }

            functionSymbol?.callableId != RUN_CATCHING_CALLABLE_ID && functionSymbol?.isInline == true
        }

        is KtValueArgument -> {
            val parentCallExpression = element.getParentOfType<KtCallExpression>(true) ?: return false
            val valueSymbol = analyze(parentCallExpression) {
                val elementArgument = element.getArgumentExpression()

                parentCallExpression.resolveToCall()
                    ?.successfulFunctionCallOrNull()
                    ?.argumentMapping
                    ?.get(elementArgument)
                    ?.symbol
            }

            valueSymbol
                ?.let {
                    it.isCrossinline.not() && it.isNoinline.not()
                }
                ?: false
        }

        else -> true
    }

    private fun KtExpression.hasSuspendCalls(): Boolean = when (this) {
        is KtForExpression -> {
            val loopRangeReferences = analyze(this) {
                mainReference?.resolveToSymbols()
                    ?.filterIsInstance<KaNamedFunctionSymbol>()
            }.orEmpty()
            loopRangeReferences.any { it.isSuspend }
        }

        is KtCallExpression, is KtOperationExpression -> {
            analyze(this) {
                resolveToCall()
                    ?.successfulCallOrNull<KaCompoundVariableAccessCall>()
                    ?.compoundOperation
                    ?.operationPartiallyAppliedSymbol
                    ?.signature
                    ?.symbol?.isSuspend

                    ?: (resolveToCall()
                        ?.successfulFunctionCallOrNull()
                        ?.symbol as? KaNamedFunctionSymbol)
                        ?.isSuspend

                    ?: false
            }
        }

        is KtNameReferenceExpression -> {
            analyze(this) {
                resolveToCall()
                    ?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
                    ?.symbol
                    ?.callableId == COROUTINE_CONTEXT_CALLABLE_ID
            }
        }

        else -> {
            false
        }
    }

    private fun KtParameter.isCancellationExceptionOrSuperClass(): Boolean = analyze(this) {
        val parameterFqName = typeReference
            ?.type
            ?.symbol
            ?.classId
            ?.asFqNameString()

        parameterFqName in CANCELLATION_EXCEPTION_FQ_NAMES
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
            .map { expr ->
                analyze(expr) {
                    expr.mainReference.resolveToSymbol()?.psi
                }
            }
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
            Finding(
                Entity.from((expression.calleeExpression as? PsiElement) ?: expression),
                "The `runCatching` has suspend call inside. You should either use specific `try-catch` " +
                    "only catching exception that you are expecting or rethrow the `CancellationException` if " +
                    "already caught."
            )
        )
    }

    private fun report(catchClause: KtCatchClause) {
        report(
            Finding(
                entity = Entity.from(catchClause),
                message = "You should always catch and re-throw CancellationExceptions in" +
                    " a try block from a suspending function. The exception should be re-thrown" +
                    " immediately after catching it - it's intended to completely kill any" +
                    " running jobs in your coroutine.",
            )
        )
    }

    companion object {
        private val RUN_CATCHING_CALLABLE_ID = CallableId(
            packageName = FqName("kotlin"),
            callableName = Name.identifier("runCatching")
        )

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

        private val COROUTINE_CONTEXT_CALLABLE_ID = CallableId(
            packageName = COROUTINES_PACKAGE_FQ_NAME,
            callableName = Name.identifier("coroutineContext")
        )
    }
}
