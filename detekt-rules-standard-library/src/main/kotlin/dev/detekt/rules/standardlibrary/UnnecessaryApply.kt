package dev.detekt.rules.standardlibrary

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.receiverIsUsed
import org.jetbrains.kotlin.analysis.api.KaContextParameterApi
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaImplicitReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.resolveSuccessfulCall
import org.jetbrains.kotlin.analysis.api.resolution.resolveSuccessfulSymbol
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.containingSymbol
import org.jetbrains.kotlin.analysis.api.symbols.symbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression
import org.jetbrains.kotlin.psi.KtThisExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

/**
 * `apply` expressions are used frequently, but sometimes their usage should be replaced with
 * an ordinary method/extension function call to reduce visual complexity
 *
 * <noncompliant>
 * config.apply { version = "1.2" } // can be replaced with `config.version = "1.2"`
 * config?.apply { environment = "test" } // can be replaced with `config?.environment = "test"`
 * config?.apply { println(version) } // `apply` can be replaced by `let`
 * </noncompliant>
 *
 * <compliant>
 * config.apply {
 *     version = "1.2"
 *     environment = "test"
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class UnnecessaryApply(config: Config) :
    Rule(
        config,
        "The `apply` usage is unnecessary and can be removed."
    ),
    RequiresAnalysisApi {

    @OptIn(KaExperimentalApi::class)
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (expression.calleeExpression?.text != "apply") return

        analyze(expression) {
            if (expression.resolveCall()?.symbol?.callableId == applyCallableId &&
                expression.hasOnlyOneMemberAccessStatement() &&
                !expression.receiverIsUsed()
            ) {
                val message = if (expression.parent is KtSafeQualifiedExpression) {
                    "apply can be replaced with let or an if"
                } else {
                    "apply expression can be omitted"
                }
                report(Finding(Entity.from(expression), message))
            }
        }
    }

    @OptIn(KaContextParameterApi::class)
    @Suppress("ReturnCount")
    context(_: KaSession)
    private fun KtCallExpression.hasOnlyOneMemberAccessStatement(): Boolean {
        val lambda = lambdaArguments.firstOrNull()?.getLambdaExpression() ?: return false
        var singleStatement = lambda.bodyExpression?.statements?.singleOrNull() ?: return false

        if (singleStatement is KtBinaryExpression) {
            if (singleStatement.operationToken !in KtTokens.ALL_ASSIGNMENTS) return false

            // for an assignment expression only consider whether members on the LHS use the apply{} context
            singleStatement = singleStatement.left ?: return false
        } else if (singleStatement !is KtThisExpression &&
            singleStatement !is KtReferenceExpression &&
            singleStatement !is KtDotQualifiedExpression
        ) {
            return false
        }

        val lambdaSymbol = lambda.functionLiteral.symbol
        return singleStatement.collectDescendantsOfType<KtNameReferenceExpression> {
            val symbol = if (it.parent is KtThisExpression) {
                @OptIn(KaExperimentalApi::class)
                it.resolveSuccessfulSymbol()
            } else {
                it.implicitReceiver()
            }
            symbol?.containingSymbol == lambdaSymbol
        }.size == 1
    }

    @OptIn(KaExperimentalApi::class)
    context(_: KaSession)
    fun KtNameReferenceExpression.implicitReceiver(): KaSymbol? {
        val symbol = resolveSuccessfulCall()
        val implicitReceiver = (symbol?.dispatchReceiver ?: symbol?.extensionReceiver) as? KaImplicitReceiverValue
        return implicitReceiver?.symbol
    }

    companion object {
        private val applyCallableId = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("apply"))
    }
}
