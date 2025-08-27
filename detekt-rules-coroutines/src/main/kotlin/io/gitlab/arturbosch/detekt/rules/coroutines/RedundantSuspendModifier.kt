package io.gitlab.arturbosch.detekt.rules.coroutines

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isOpen
import io.gitlab.arturbosch.detekt.rules.coroutines.utils.CoroutineCallableIds
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.KaVariableAccessCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/*
 * Based on code from Kotlin project:
 * https://github.com/JetBrains/kotlin/blob/v1.3.61/idea/src/org/jetbrains/kotlin/idea/inspections/RedundantSuspendModifierInspection.kt
 */

/**
 * `suspend` modifier should only be used where needed, otherwise the function can only be used from other suspending
 * functions. This needlessly restricts use of the function and should be avoided by removing the `suspend` modifier
 * where it's not needed.
 *
 * <noncompliant>
 * suspend fun normalFunction() {
 *     println("string")
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun normalFunction() {
 *     println("string")
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class RedundantSuspendModifier(config: Config) :
    Rule(
        config,
        "The `suspend` modifier is only needed for functions that contain suspending calls."
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        val suspendModifier = function.modifierList?.getModifier(KtTokens.SUSPEND_KEYWORD) ?: return
        if (!function.hasBody()) return
        if (function.hasModifier(KtTokens.OVERRIDE_KEYWORD) || function.hasModifier(KtTokens.ACTUAL_KEYWORD)) return

        if (function.isOpen()) return

        if (!function.anyDescendantOfType<KtExpression> { it.hasSuspendCalls() }) {
            report(
                Finding(
                    Entity.from(suspendModifier),
                    "Function has redundant `suspend` modifier."
                )
            )
        }
    }

    private fun KtExpression.isValidCandidateExpression(): Boolean =
        when (this) {
            is KtOperationReferenceExpression, is KtForExpression, is KtProperty, is KtNameReferenceExpression -> true
            else -> {
                val parent = parent
                if (parent is KtCallExpression && parent.calleeExpression == this) {
                    true
                } else {
                    this is KtCallExpression && this.calleeExpression is KtCallExpression
                }
            }
        }

    private fun KtExpression.hasSuspendCalls(): Boolean {
        if (!isValidCandidateExpression()) return false

        return when (this) {
            is KtForExpression -> {
                analyze(this) {
                    this@hasSuspendCalls.mainReference?.run {
                        resolveToSymbols()
                            .filterIsInstance<KaNamedFunctionSymbol>()
                            .any { it.isSuspend }
                    } ?: false
                }
            }

            else -> {
                analyze(this) {
                    val call = this@hasSuspendCalls.resolveToCall()
                        ?.successfulCallOrNull<KaCallableMemberCall<*, *>>()
                        ?: return false
                    when (call) {
                        is KaFunctionCall -> {
                            (call.symbol as? KaNamedFunctionSymbol)?.isSuspend == true
                        }

                        is KaVariableAccessCall -> {
                            call.symbol.callableId == CoroutineCallableIds.CoroutineContextCallableId
                        }
                    }
                }
            }
        }
    }
}
