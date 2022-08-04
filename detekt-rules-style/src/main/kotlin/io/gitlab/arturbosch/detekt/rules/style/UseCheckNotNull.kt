package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isCallingWithNonNullCheckArgument
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Turn on this rule to flag `check` calls for not-null check that can be replaced with a `checkNotNull` call.
 *
 * <noncompliant>
 * check(x != null)
 * </noncompliant>
 *
 * <compliant>
 * checkNotNull(x)
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class UseCheckNotNull(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        "UseCheckNotNull",
        Severity.Style,
        "Use checkNotNull() instead of check() for checking not-null.",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.isCallingWithNonNullCheckArgument(checkFunctionFqName, bindingContext)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    companion object {
        private val checkFunctionFqName = FqName("kotlin.check")
    }
}
