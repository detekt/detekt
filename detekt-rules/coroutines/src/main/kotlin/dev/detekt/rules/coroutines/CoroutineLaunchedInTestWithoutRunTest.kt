package dev.detekt.rules.coroutines

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.hasAnnotation
import dev.detekt.rules.coroutines.utils.isCoroutineScope
import dev.detekt.rules.coroutines.utils.isCoroutinesFlow
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getCalleeExpressionIfAny

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
    RequiresAnalysisApi {

    private val funCoroutineLaunchesTraverseHelper = FunCoroutineLaunchesTraverseHelper()

    override fun visitNamedFunction(initialFunction: KtNamedFunction) {
        if (!initialFunction.hasBody()) return
        if (!initialFunction.hasAnnotation(TEST_ANNOTATION_NAME)) return
        analyze(initialFunction) {
            if (initialFunction.runsInRunTestBlock()) return
            // By this point we know we're inside a test function that is not a `runTest` function.
            if (funCoroutineLaunchesTraverseHelper.isFunctionLaunchingCoroutines(initialFunction)) {
                report(Finding(Entity.from(initialFunction), MESSAGE))
            }
        }
    }

    context(session: KaSession)
    private fun KtNamedFunction.runsInRunTestBlock(): Boolean =
        with(session) {
            bodyExpression?.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId == RUN_TEST_CALLABLE_ID
        }

    companion object {
        private const val MESSAGE =
            "Launching coroutines in tests without a `runTest` block."

        private const val TEST_ANNOTATION_NAME = "Test"
        private val RUN_TEST_CALLABLE_ID = CallableId(FqName("kotlinx.coroutines.test"), Name.identifier("runTest"))
    }
}

class FunCoroutineLaunchesTraverseHelper {
    val exploredFunctionsCache = mutableMapOf<KtNamedFunction, Boolean>()

    context(session: KaSession)
    fun isFunctionLaunchingCoroutines(initialFunction: KtNamedFunction): Boolean {
        val traversedFunctions = mutableSetOf<KtNamedFunction>()

        fun checkFunctionAndSaveToCache(function: KtNamedFunction, parents: List<KtNamedFunction> = emptyList()) {
            val isLaunching = function.isLaunchingCoroutine()
            exploredFunctionsCache.putIfAbsent(function, isLaunching)

            if (isLaunching) {
                parents.forEach { exploredFunctionsCache[it] = true }
            }
        }

        fun getChildFunctionsOf(
            function: KtNamedFunction,
            parents: List<KtNamedFunction> = emptyList(),
        ): Set<KtNamedFunction> {
            function.collectDescendantsOfType<KtExpression>().mapNotNull {
                with(session) {
                    it.resolveToCall()?.singleFunctionCallOrNull()?.symbol?.psi as? KtNamedFunction
                }
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

    context(session: KaSession)
    private fun KtNamedFunction.isLaunchingCoroutine() =
        with(session) {
            anyDescendantOfType<KtDotQualifiedExpression> {
                val receiverType = it.receiverExpression.expressionType ?: return@anyDescendantOfType false
                val calleeText = it.getCalleeExpressionIfAny()?.text ?: return@anyDescendantOfType false
                (receiverType.isCoroutineScope() && calleeText in listOf("launch", "async")) ||
                    (receiverType.isCoroutinesFlow() && calleeText == "launchIn")
            }
        }
}
