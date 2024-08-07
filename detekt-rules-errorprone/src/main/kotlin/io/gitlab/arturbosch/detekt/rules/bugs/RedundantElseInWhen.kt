package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Reports `when` expressions that contain a redundant `else` case. This occurs when it can be
 * verified that all cases are already covered when checking cases on an enum or sealed class.
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
 */
@ActiveByDefault(since = "1.2.0")
@Deprecated("Rule deprecated as compiler performs this check by default")
class RedundantElseInWhen(config: Config) :
    Rule(
        config,
        "Check for redundant `else` case in `when` expression when used as statement."
    ),
    RequiresTypeResolution {
    override fun visitWhenExpression(whenExpression: KtWhenExpression) {
        super.visitWhenExpression(whenExpression)

        val elseEntry = whenExpression.entries.lastOrNull { it.isElse } ?: return
        val compilerReports = bindingContext.diagnostics.forElement(elseEntry)
        if (compilerReports.any { it.factory == Errors.REDUNDANT_ELSE_IN_WHEN }) {
            report(CodeSmell(Entity.from(whenExpression), "When expression contains redundant `else` case."))
        }
    }
}
