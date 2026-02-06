package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCallingWithNonNullCheckArgument
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Turn on this rule to flag `require` calls for not-null check that can be replaced with a `requireNotNull` call.
 *
 * <noncompliant>
 * require(x != null)
 * </noncompliant>
 *
 * <compliant>
 * requireNotNull(x)
 * </compliant>
 */
@ActiveByDefault(since = "1.21.0")
class UseRequireNotNull(config: Config) :
    Rule(
        config,
        "Use requireNotNull() instead of require() for checking not-null."
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.isCallingWithNonNullCheckArgument(requireFunctionCallableId)) {
            report(Finding(Entity.from(expression), description))
        }
    }

    companion object {
        private val requireFunctionCallableId =
            CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("require"))
    }
}
