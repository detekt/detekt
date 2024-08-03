package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Functions that return `Flow` from `kotlinx.coroutines.flow` should not be marked as `suspend`.
 * `Flows` are intended to be cold observable streams. The act of simply invoking a function that
 * returns a `Flow`, should not have any side effects. Only once collection begins against the
 * returned `Flow`, should work actually be done.
 *
 * See https://kotlinlang.org/docs/flow.html#flows-are-cold
 *
 * <noncompliant>
 * suspend fun observeSignals(): Flow<Unit> {
 *     val pollingInterval = getPollingInterval() // Done outside of the flow builder block.
 *     return flow {
 *         while (true) {
 *             delay(pollingInterval)
 *             emit(Unit)
 *         }
 *     }
 * }
 *
 * private suspend fun getPollingInterval(): Long {
 *     // Return the polling interval from some repository
 *     // in a suspending manner.
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun observeSignals(): Flow<Unit> {
 *     return flow {
 *         val pollingInterval = getPollingInterval() // Moved into the flow builder block.
 *         while (true) {
 *             delay(pollingInterval)
 *             emit(Unit)
 *         }
 *     }
 * }
 *
 * private suspend fun getPollingInterval(): Long {
 *     // Return the polling interval from some repository
 *     // in a suspending manner.
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class SuspendFunWithFlowReturnType(config: Config) :
    Rule(
        config,
        "The `suspend` modifier should not be used for functions that return a Coroutines Flow type. Flows are cold " +
            "streams and invoking a function that returns one should not produce any side effects."
    ),
    RequiresTypeResolution {
    override fun visitNamedFunction(function: KtNamedFunction) {
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        bindingContext[BindingContext.FUNCTION, function]
            ?.returnType
            ?.takeIf { it.isCoroutinesFlow() }
            ?.also {
                report(
                    CodeSmell(
                        entity = Entity.from(suspendModifier),
                        message = "`suspend` function returns Coroutines Flow."
                    )
                )
            }
    }

    private fun KotlinType.isCoroutinesFlow(): Boolean =
        sequence {
            yield(this@isCoroutinesFlow)
            yieldAll(this@isCoroutinesFlow.supertypes())
        }
            .mapNotNull { it.fqNameOrNull()?.asString() }
            .contains("kotlinx.coroutines.flow.Flow")
}
