package dev.detekt.rules.coroutines

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.allConstructors
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

/**
 * Always use dependency injection to inject dispatchers for easier testing.
 * This rule is based on the recommendation
 * https://developer.android.com/kotlin/coroutines/coroutines-best-practices#inject-dispatchers
 *
 * <noncompliant>
 * fun myFunc() {
 *     coroutineScope(Dispatchers.IO)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun myFunc(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
 *     coroutineScope(dispatcher)
 * }
 *
 * class MyRepository(dispatchers: CoroutineDispatcher = Dispatchers.IO)
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class InjectDispatcher(config: Config) :
    Rule(
        config,
        "Don't hardcode dispatchers when creating new coroutines or calling `withContext`. " +
            "Use dependency injection for dispatchers to make testing easier."
    ),
    RequiresAnalysisApi {

    @Configuration("The names of dispatchers to detect by this rule")
    private val dispatcherNames: Set<String> by config(listOf("IO", "Default", "Unconfined")) { it.toSet() }

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        if (expression.getReferencedName() !in dispatcherNames) return
        if (analyze(expression) { expression.expressionType?.isSubtypeOf(COROUTINE_DISPATCHER) } != true) return
        val isUsedAsParameter = expression.getStrictParentOfType<KtParameter>() != null ||
            expression.getStrictParentOfType<KtConstructorDelegationCall>() != null
        if (!isUsedAsParameter) {
            if (expression.isReceiverNotInjected()) return
            report(
                Finding(
                    Entity.from(expression),
                    "Dispatcher ${expression.getReferencedName()} is used without dependency injection."
                )
            )
        }
    }

    private fun KtSimpleNameExpression.isReceiverNotInjected(): Boolean {
        val receiver = getReceiverExpression() ?: return false
        val receiverTypeFqn = analyze(receiver) { receiver.expressionType?.symbol?.classId }?.asFqNameString()
            ?: return false
        return isAClassPropertyOrConstructorParameter(receiver = receiver.text, receiverTypeFqn = receiverTypeFqn) ||
            isAFunctionParameter(receiver = receiver.text, receiverTypeFqn = receiverTypeFqn)
    }

    private fun KtSimpleNameExpression.isAClassPropertyOrConstructorParameter(
        receiver: String,
        receiverTypeFqn: String,
    ): Boolean {
        val enclosingClass = getStrictParentOfType<KtClassOrObject>()
        val property = enclosingClass?.findPropertyByName(receiver) as? KtParameter
        if (property != null && property.typeReference?.getTypeText() == receiverTypeFqn) return true

        val ctor = enclosingClass?.allConstructors?.firstNotNullOfOrNull { ctor ->
            ctor.getValueParameters().find { it.name == receiver }
        }

        return ctor != null && ctor.typeReference?.getTypeText() == receiverTypeFqn
    }

    private fun KtSimpleNameExpression.isAFunctionParameter(
        receiver: String,
        receiverTypeFqn: String,
    ): Boolean {
        val enclosingFunction = getStrictParentOfType<KtNamedFunction>()
        val param = enclosingFunction?.valueParameters?.find { it.name == receiver }
        return param != null && param.typeReference?.getTypeText() == receiverTypeFqn
    }

    companion object {
        private val COROUTINE_DISPATCHER = ClassId.fromString("kotlinx/coroutines/CoroutineDispatcher")
    }
}
