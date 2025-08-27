package dev.detekt.rules.coroutines

import dev.detekt.api.Alias
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.rules.coroutines.utils.CoroutineClassIds
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Suspend functions that use `CoroutineScope` as receiver should not be marked as `suspend`.
 * A `CoroutineScope` provides structured concurrency via its `coroutineContext`. A `suspend`
 * function also has its own `coroutineContext`, which is now ambiguous and mixed with the
 * receiver`s.
 *
 * See https://kotlinlang.org/docs/coroutines-basics.html#scope-builder-and-concurrency
 *
 * <noncompliant>
 * suspend fun CoroutineScope.foo() {
 *     launch {
 *         delay(1.seconds)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun CoroutineScope.foo() {
 *     launch {
 *         delay(1.seconds)
 *     }
 * }
 *
 * // Alternative
 * suspend fun foo() = coroutineScope {
 *     launch {
 *         delay(1.seconds)
 *     }
 * }
 * </compliant>
 *
 */
@Alias("SuspendFunctionOnCoroutineScope")
class SuspendFunWithCoroutineScopeReceiver(config: Config) :
    Rule(
        config,
        "The `suspend` modifier should not be used for functions that use a CoroutinesScope as receiver. You should " +
            "use suspend functions without the receiver or use plain functions and use coroutineScope { } instead."
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        checkReceiver(function)
    }

    private fun checkReceiver(function: KtNamedFunction) {
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        val isCoroutineScope = analyze(function) {
            function.receiverTypeReference?.type?.isSubtypeOf(CoroutineClassIds.CoroutineScope) == true
        }
        if (isCoroutineScope) {
            report(
                Finding(
                    entity = Entity.from(suspendModifier),
                    message = "`suspend` function uses CoroutineScope as receiver."
                )
            )
        }
    }
}
