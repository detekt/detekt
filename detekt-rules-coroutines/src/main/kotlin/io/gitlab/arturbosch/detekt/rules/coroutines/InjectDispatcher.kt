package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtConstructorDelegationCall
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
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
 *  coroutineScope(Dispatchers.IO)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun myFunc(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
 *  coroutineScope(dispatcher)
 * }
 *
 * class MyRepository(dispatchers: CoroutineDispatcher = Dispatchers.IO)
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class InjectDispatcher(config: Config) : Rule(config) {

    @Configuration("The names of dispatchers to detect by this rule")
    private val dispatcherNames: Set<String> by config(listOf("IO", "Default", "Unconfined")) { it.toSet() }

    override val issue = Issue(
        "InjectDispatcher",
        Severity.Defect,
        "Don't hardcode dispatchers when creating new coroutines or calling `withContext`. " +
            "Use dependency injection for dispatchers to make testing easier.",
        Debt.FIVE_MINS
    )

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        if (expression.getReferencedName() !in dispatcherNames) return
        val type = expression.getType(bindingContext) ?: return
        val isCoroutineDispatcher = type.fqNameOrNull() == COROUTINE_DISPATCHER_FQCN ||
            type.supertypes().any { it.fqNameOrNull() == COROUTINE_DISPATCHER_FQCN }
        val isUsedAsParameter = expression.getStrictParentOfType<KtParameter>() != null ||
            expression.getStrictParentOfType<KtConstructorDelegationCall>() != null
        if (isCoroutineDispatcher && !isUsedAsParameter) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Dispatcher ${expression.getReferencedName()} is used without dependency injection."
                )
            )
        }
    }

    companion object {
        private val COROUTINE_DISPATCHER_FQCN = FqName("kotlinx.coroutines.CoroutineDispatcher")
    }
}
