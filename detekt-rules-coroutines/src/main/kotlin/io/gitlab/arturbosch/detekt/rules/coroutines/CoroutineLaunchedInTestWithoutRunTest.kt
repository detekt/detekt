package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import io.gitlab.arturbosch.detekt.rules.hasAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.source.getPsi

/**
 * Detect coroutine launches from `@Test` functions outside a `runTest` block.
 * Launching coroutines in tests without using `runTest` could potentially swallow exceptions,
 * altering test results or causing crashes on other unrelated tests.
 *
 * <noncompliant>
 * @@Test
 * fun `test that launches a coroutine`() {
 *     val scope = CoroutineScope(Dispatchers.Unconfined)
 *     scope.launch {
 *         suspendFunction()
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * @@Test
 * fun `test that launches a coroutine`() = runTest {
 *     launch {
 *         suspendFunction()
 *     }
 * }
 * </compliant>
 *
 */
class CoroutineLaunchedInTestWithoutRunTest(config: Config) :
    Rule(
        config,
        "Launching coroutines in tests without a `runTest` block could swallow exceptions. " +
            "You should use `runTest` to avoid altering test results."
    ),
    RequiresTypeResolution {
    private val funCoroutineLaunchesTraverseHelper = FunCoroutineLaunchesTraverseHelper()

    override fun visitNamedFunction(initialFunction: KtNamedFunction) {
        if (!initialFunction.hasBody()) return
        if (!initialFunction.hasAnnotation(TEST_ANNOTATION_NAME)) return
        if (initialFunction.runsInRunTestBlock()) return

        // By this point we know we're inside a test function that is not a `runTest` function.
        if (funCoroutineLaunchesTraverseHelper.isFunctionLaunchingCoroutines(initialFunction, bindingContext)) {
            report(CodeSmell(Entity.from(initialFunction), MESSAGE))
        }
    }

    private fun KtNamedFunction.runsInRunTestBlock() =
        bodyExpression
            .getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameSafe == RUN_TEST_FQ

    companion object {
        private const val MESSAGE =
            "Launching coroutines in tests without a `runTest` block."

        private const val TEST_ANNOTATION_NAME = "Test"
        private val RUN_TEST_FQ = FqName("kotlinx.coroutines.test.runTest")
    }
}

class FunCoroutineLaunchesTraverseHelper {
    val exploredFunctionsCache = mutableMapOf<KtNamedFunction, Boolean>()

    fun isFunctionLaunchingCoroutines(
        initialFunction: KtNamedFunction,
        bindingContext: BindingContext
    ): Boolean {
        val traversedFunctions = mutableSetOf<KtNamedFunction>()

        fun checkFunctionAndSaveToCache(
            function: KtNamedFunction,
            parents: List<KtNamedFunction> = emptyList()
        ) {
            val isLaunching = function.isLaunchingCoroutine(bindingContext)
            exploredFunctionsCache.putIfAbsent(function, isLaunching)

            if (isLaunching) {
                parents.forEach { exploredFunctionsCache[it] = true }
            }
        }

        fun getChildFunctionsOf(
            function: KtNamedFunction,
            parents: List<KtNamedFunction> = emptyList()
        ): Set<KtNamedFunction> {
            function.collectDescendantsOfType<KtExpression>().mapNotNull {
                it.getResolvedCall(bindingContext)
                    ?.resultingDescriptor
                    ?.source
                    ?.getPsi() as? KtNamedFunction
            }.forEach {
                traversedFunctions.add(it)
                if (exploredFunctionsCache.contains(it)) return@forEach

                checkFunctionAndSaveToCache(it, parents)

                getChildFunctionsOf(it, parents + it).forEach { childFunction ->
                    checkFunctionAndSaveToCache(childFunction, parents)
                }
            }

            return traversedFunctions
        }

        traversedFunctions.add(initialFunction)
        checkFunctionAndSaveToCache(initialFunction)
        getChildFunctionsOf(initialFunction, listOf(initialFunction))

        return traversedFunctions.any { exploredFunctionsCache[it] == true }
    }

    private fun KtNamedFunction.isLaunchingCoroutine(bindingContext: BindingContext) =
        anyDescendantOfType<KtDotQualifiedExpression> {
            it.receiverExpression
                .getType(bindingContext)
                ?.fqNameOrNull() == COROUTINE_SCOPE_FQ &&
                it.getCalleeExpressionIfAny()?.text == "launch"
        }

    companion object {
        private val COROUTINE_SCOPE_FQ = FqName("kotlinx.coroutines.CoroutineScope")
    }
}
