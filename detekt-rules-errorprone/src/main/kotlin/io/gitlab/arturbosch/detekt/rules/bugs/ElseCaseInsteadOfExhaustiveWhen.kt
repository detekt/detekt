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
import org.jetbrains.kotlin.types.typeUtil.isBooleanOrNullableBoolean

/**
 * This rule reports `when` expressions that contain an `else` case even though they have a limited set of cases.
 *
 * This occurs when the subject of the `when` expression is either an enum class, sealed class or of type boolean.
 * Using `else` cases for these expressions can lead to unwanted behavior when adding new enum types, sealed subtype
 * or changing the nullability of a boolean, since this will be covered by the `else` case.
 *
 * <noncompliant>
 * enum class Color {
 *     RED,
 *     GREEN,
 *     BLUE
 * }
 *
 * when(c) {
 *     Color.RED -> {}
 *     Color.GREEN -> {}
 *     else -> {}
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
 * when(c) {
 *     Color.RED -> {}
 *     Color.GREEN -> {}
 *     Color.BLUE -> {}
 * }
 * </compliant>
 */
@RequiresTypeResolution
class ElseCaseInsteadOfExhaustiveWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "ElseCaseInsteadOfExhaustiveWhen",
        Severity.Warning,
        "A `when` expression that has a limited set of cases should not contain an `else` case.",
        Debt.FIVE_MINS
    )

    override fun visitWhenExpression(whenExpression: KtWhenExpression) {
        super.visitWhenExpression(whenExpression)

        if (bindingContext == BindingContext.EMPTY) return
        checkForElseCaseInsteadOfExhaustiveWhenExpression(whenExpression)
    }

    private fun checkForElseCaseInsteadOfExhaustiveWhenExpression(whenExpression: KtWhenExpression) {
        val subjectExpression = whenExpression.subjectExpression ?: return
        if (whenExpression.elseExpression == null) return

        val subjectType = subjectExpression.getType(bindingContext)
        val isEnumSubject = WhenChecker.getClassDescriptorOfTypeIfEnum(subjectType) != null
        val isSealedSubject = WhenChecker.getClassDescriptorOfTypeIfSealed(subjectType) != null
        val isBooleanSubject = subjectType?.isBooleanOrNullableBoolean() == true

        if (isEnumSubject || isSealedSubject || isBooleanSubject) {
            val subjectTypeName = when {
                isEnumSubject -> "enum class"
                isSealedSubject -> "sealed class"
                else -> "boolean"
            }
            val message = "When expression with $subjectTypeName subject should not contain an `else` case."
            report(CodeSmell(issue, Entity.from(whenExpression), message))
        }
    }
}
