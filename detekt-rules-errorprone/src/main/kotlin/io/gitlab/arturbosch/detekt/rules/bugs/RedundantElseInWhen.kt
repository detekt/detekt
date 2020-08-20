package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingContext.DECLARATION_TO_DESCRIPTOR
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.source.getPsi

/**
 * Turn on this rule to flag `when` expressions that contain a redundant `else` case. This occurs when it can be
 * verified that all cases are already covered when checking cases on an enum or sealed class.
 *
 * Based on code from Kotlin compiler:
 * https://github.com/JetBrains/kotlin/blob/v1.3.30/compiler/frontend/src/org/jetbrains/kotlin/cfg/ControlFlowInformationProvider.kt
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
 *         Color.RED -> {}
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
 *         else -> {}
 *     }
 * }
 *
 * fun whenOnEnumCompliant2(c: Color) {
 *     when(c) {
 *         Color.BLUE -> {}
 *         Color.GREEN -> {}
 *         Color.RED -> {}
 *     }
 * }
 * </compliant>
 *
 * @active since v1.2.0
 * @requiresTypeResolution
 */
class RedundantElseInWhen(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "RedundantElseInWhen",
        Severity.Warning,
        "Check for redundant `else` case in `when` expression when used as statement.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitWhenExpression(whenExpression: KtWhenExpression) {
        if (bindingContext == BindingContext.EMPTY) return
        if (whenExpression.elseExpression == null) return
        val subjectExpression = whenExpression.subjectExpression ?: return
        val subjectType = subjectExpression.getType(bindingContext) ?: return

        if (WhenChecker.getMissingCases(whenExpression, bindingContext).isEmpty()) {
            val subjectClass = subjectType.constructor.declarationDescriptor as? ClassDescriptor
            val pseudocodeDescriptor =
                bindingContext[DECLARATION_TO_DESCRIPTOR, subjectClass?.toSourceElement?.getPsi()]
            if (subjectClass == null ||
                KotlinBuiltIns.isBooleanOrNullableBoolean(subjectType) ||
                subjectClass.module == pseudocodeDescriptor?.module
            ) {
                report(CodeSmell(issue, Entity.from(whenExpression), "When expression contains redundant `else` case."))
            }
        }
        super.visitWhenExpression(whenExpression)
    }
}
