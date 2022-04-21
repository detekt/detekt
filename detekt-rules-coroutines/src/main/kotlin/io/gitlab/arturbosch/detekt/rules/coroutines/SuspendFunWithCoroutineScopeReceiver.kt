package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.builtins.getReceiverTypeFromFunctionType
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.supertypes

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
 *       delay(1.seconds)
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun CoroutineScope.foo() {
 *     launch {
 *       delay(1.seconds)
 *     }
 * }
 *
 * // Alternative
 * suspend fun foo() = coroutineScope {
 *     launch {
 *       delay(1.seconds)
 *     }
 * }
 * </compliant>
 *
 */
@RequiresTypeResolution
class SuspendFunWithCoroutineScopeReceiver(config: Config) : Rule(config) {

    override val issue = Issue(
        id = "SuspendFunWithCoroutineScopeReceiver",
        severity = Severity.Minor,
        description = "The `suspend` modifier should not be used for functions that use a " +
            "CoroutinesScope as receiver. You should use suspend functions without the receiver or use plain " +
            "functions and use coroutineScope { } instead.",
        debt = Debt.TEN_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) return
        checkReceiver(function)
        checkLambdaParameters(function.valueParameters)
    }

    private fun checkLambdaParameters(parameters: List<KtParameter>) {
        for (it in parameters) {
            val type = bindingContext[BindingContext.VALUE_PARAMETER, it]
                ?.type?.takeIf { it.isSuspendFunctionType } ?: continue
            if (type.getReceiverTypeFromFunctionType()?.isCoroutineScope() == true) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.Companion.from(it),
                        message = "`suspend` function uses CoroutineScope as receiver."
                    )
                )
            }
        }
    }

    private fun checkReceiver(function: KtNamedFunction) {
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        val receiver = bindingContext[BindingContext.FUNCTION, function]
            ?.extensionReceiverParameter?.value?.type ?: return
        if (receiver.isCoroutineScope()) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(suspendModifier),
                    message = "`suspend` function uses CoroutineScope as receiver."
                )
            )
        }
    }

    private fun KotlinType.isCoroutineScope() = sequence {
        yield(this@isCoroutineScope)
        yieldAll(this@isCoroutineScope.supertypes())
    }
        .mapNotNull { it.fqNameOrNull()?.asString() }
        .contains("kotlinx.coroutines.CoroutineScope")
}
