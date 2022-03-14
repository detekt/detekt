package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Turn on this rule to flag `when` expressions with an `enum` or `sealed` class subject that contain an `else` case.
 *
 * <noncompliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * fun whenOnEnumFail(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         Color.GREEN -> {}
 *         else -> {}
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * fun whenOnEnumCompliant(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         Color.GREEN -> {}
 *         Color.RED -> {}
 *     }
 * }
 * </compliant>
 */
@RequiresTypeResolution
class ElseCaseInEnumOrSealedWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "ElseCaseInEnumOrSealedWhen",
        Severity.Warning,
        "Check for `else` case in enum or sealed `when` expression",
        Debt.FIVE_MINS
    )

    override fun visitWhenExpression(whenExpression: KtWhenExpression) {
        super.visitWhenExpression(whenExpression)

        if (bindingContext == BindingContext.EMPTY) return
        checkForElseCaseInEnumOrSealedWhenExpression(whenExpression)
    }

    private fun checkForElseCaseInEnumOrSealedWhenExpression(whenExpression: KtWhenExpression) {
        val subjectExpression = whenExpression.subjectExpression ?: return
        if (whenExpression.elseExpression == null) return

        val subjectType = subjectExpression.getType(bindingContext)
        val isEnumSubject = WhenChecker.getClassDescriptorOfTypeIfEnum(subjectType) != null
        val isSealedSubject = WhenChecker.getClassDescriptorOfTypeIfSealed(subjectType) != null

        if (isEnumSubject || isSealedSubject) {
            val subjectTypeName = if (isEnumSubject) "enum class" else "sealed class"
            val message = "When expression with $subjectTypeName subject contains `else` case."
            report(CodeSmell(issue, Entity.from(whenExpression), message))
        }
    }
}
