package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.BASE_COLLECTIONS_PACKAGE
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * If a sort operation followed by a reverse operation or vise versa should be avoided, and both statements
 * should be replaced by single equivalent sort operation.
 *
 * <noncompliant>
 * listOf(1,2)
 *  .sorted()
 *  .asReversed()
 * </noncompliant>
 *
 * <compliant>
 * listOf(1,2)
 *  .sortedDescending()
 * </compliant>
 */
class UnnecessaryReversed(config: Config) :
    Rule(
        config,
        "Use single sort operation instead of sorting followed by a reverse operation or vise-versa, " +
            "eg. use `.sortedByDescending { .. }` instead of `.sortedBy { }.asReversed()`",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(sortFunctions + reverseFunctions)) return

        val callId = expression.callableId() ?: return
        val parentCalls = expression.getPrevCallInChainOrNull()

        val oppositeCalls = if (callId in sortFunctions) reverseFunctions else sortFunctions

        val parentCall = parentCalls.find { parentExpression ->
            parentExpression.isCalling(oppositeCalls)
        } ?: return

        val parentCallId = parentCall.callableId() ?: return

        val sortCallUsed = if (parentCallId in sortFunctions) parentCallId else callId

        val isSequentialCall = parentCallId == parentCalls.lastOrNull()?.callableId()

        val suggestion = oppositePairs[sortCallUsed]?.let {
            if (isSequentialCall) {
                "Replace `${parentCallId.callableName}().${callId.callableName}()` by a single `${it.callableName}()`"
            } else {
                "Replace `${callId.callableName}()` following `${parentCallId.callableName}()` by a " +
                    "single `${it.callableName}() call`"
            }
        } ?: description

        report(
            Finding(
                entity = Entity.from(expression),
                message = suggestion,
                references = listOf(Entity.from(expression), Entity.from(parentCall)),
            ),
        )
    }

    private fun KtCallExpression.callableId(): CallableId? = analyze(this) {
        resolveToCall()?.singleFunctionCallOrNull()?.symbol?.callableId
    }

    private fun KtExpression.getPrevCallInChainOrNull(): List<KtCallExpression> =
        parent.collectDescendantsOfType<KtCallExpression>()
            .dropLastWhile { it.psiOrParent == psiOrParent }

    companion object {
        private val reverseFunctions = listOf("reversed", "asReversed")
            .map { it.collectionsCall() }

        private val sortFunctions = listOf("sortedBy", "sortedByDescending", "sorted", "sortedDescending")
            .map { it.collectionsCall() }

        private val oppositePairs = mapOf(
            "sortedBy".collectionsCall() to "sortedByDescending".collectionsCall(),
            "sorted".collectionsCall() to "sortedDescending".collectionsCall()
        ).let { map ->
            map + map.map { it.value to it.key }.toMap()
        }

        private fun String.collectionsCall() = CallableId(BASE_COLLECTIONS_PACKAGE, Name.identifier(this))
    }
}
