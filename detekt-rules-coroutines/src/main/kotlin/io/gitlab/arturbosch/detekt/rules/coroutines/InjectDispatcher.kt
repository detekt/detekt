package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.allConstructors
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.types.typeUtil.supertypes

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
    RequiresFullAnalysis {

    @Configuration("The names of dispatchers to detect by this rule")
    private val dispatcherNames: Set<String> by config(listOf("IO", "Default", "Unconfined")) { it.toSet() }

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        if (expression.getReferencedName() !in dispatcherNames) return
        val type = expression.getType(bindingContext) ?: return
        val isCoroutineDispatcher = type.fqNameOrNull() == COROUTINE_DISPATCHER_FQCN ||
            type.supertypes().any { it.fqNameOrNull() == COROUTINE_DISPATCHER_FQCN }
        val isUsedAsParameter = expression.getStrictParentOfType<KtParameter>() != null ||
            expression.getStrictParentOfType<KtConstructorDelegationCall>() != null
        if (isCoroutineDispatcher && !isUsedAsParameter) {
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
        val receiver = getReceiverExpression()
        if (receiver != null) {
            val receiverTypeFqn = receiver.getType(bindingContext)?.fqNameOrNull()
            if (receiverTypeFqn != null) {
                return isAClassPropertyOrConstructorParameter(
                    receiver = receiver.text,
                    receiverTypeFqn = receiverTypeFqn.asString(),
                ) ||
                    isAFunctionParameter(
                        receiver = receiver.text,
                        receiverTypeFqn = receiverTypeFqn.asString(),
                    )
            }
        }

        return false
    }

    private fun KtSimpleNameExpression.isAClassPropertyOrConstructorParameter(
        receiver: String,
        receiverTypeFqn: String?,
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
        receiverTypeFqn: String?,
    ): Boolean {
        val enclosingFunction = getStrictParentOfType<KtNamedFunction>()
        val param = enclosingFunction?.valueParameters?.find { it.name == receiver } as? KtParameter
        return param != null && param.typeReference?.getTypeText() == receiverTypeFqn
    }

    companion object {
        private val COROUTINE_DISPATCHER_FQCN = FqName("kotlinx.coroutines.CoroutineDispatcher")
    }
}
