package io.gitlab.arturbosch.detekt.rules.coroutines

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Report usages of GlobalScope. Usage of GlobalScope is highly discouraged by the Kotlin documentation.
 * See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/
 *
 * <noncompliant>
 * GlobalScope.launch { delay(1_000L) }
 * </noncompliant>
 *
 * <compliant>
 * CoroutineScope(Dispatchers.Default).launch { delay(1_000L) }
 * </compliant>
 */
class GlobalScopeUsage(config: Config = Config.empty) : Rule(config) {
    companion object {
        private const val GLOBALSCOPE = "GlobalScope"
        private const val WANTED_TYPE = "kotlinx.coroutines.$GLOBALSCOPE"
        private const val MESSAGE =
            "This use of GlobalScope should be replaced by `CoroutineScope` or `coroutineScope`."
    }

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Usage of GlobalScope instance is highly discouraged",
        Debt.TEN_MINS
    )

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitExpression(expression)

        if (bindingContext != BindingContext.EMPTY && expression.text == GLOBALSCOPE) {
            val type = expression.getType(bindingContext)?.constructor?.declarationDescriptor as? ClassDescriptor

            if (type?.fqNameOrNull()?.asString() == WANTED_TYPE) {
                report(CodeSmell(issue, Entity.from(expression), MESSAGE))
            }
        }
    }
}
