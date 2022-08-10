package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.types.typeUtil.TypeNullability
import org.jetbrains.kotlin.types.typeUtil.nullability

/**
 * Reports unnecessary not-null checks with `requireNotNull` or `checkNotNull` that can be removed by the user.
 *
 * <noncompliant>
 * var string = "foo"
 * println(requireNotNull(string))
 * </noncompliant>
 *
 * <compliant>
 * var string : String? = "foo"
 * println(requireNotNull(string))
 * </compliant>
 */
@RequiresTypeResolution
class UnnecessaryNotNullCheck(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UnnecessaryNotNullCheck",
        Severity.Defect,
        "Unnecessary not-null check detected.",
        Debt.FIVE_MINS,
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (bindingContext == BindingContext.EMPTY) return

        val callName = expression.getCallNameExpression()?.text
        if (callName == "requireNotNull" || callName == "checkNotNull") {
            val type = (expression.valueArguments[0].lastChild as KtExpression).getType(bindingContext)
            if (type?.nullability() == TypeNullability.NOT_NULL) {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(expression),
                        message = "${expression.text} contains an unnecessary `$callName`",
                    )
                )
            }
        }
    }

}
