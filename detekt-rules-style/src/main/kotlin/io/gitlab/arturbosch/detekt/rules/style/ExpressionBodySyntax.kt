package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.anyDescendantOfType

/**
 * Functions which only contain a `return` statement can be collapsed to an expression body. This shortens and
 * cleans up the code.
 *
 * <noncompliant>
 * fun stuff(): Int {
 *     return 5
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun stuff() = 5
 *
 * fun stuff() {
 *     return
 *         moreStuff()
 *             .getStuff()
 *             .stuffStuff()
 * }
 * </compliant>
 *
 * @configuration includeLineWrapping - include return statements with line wraps in it (default: `false`)
 */
class ExpressionBodySyntax(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Functions with exact one statement, the return statement," +
            " can be rewritten with ExpressionBodySyntax.",
        Debt.FIVE_MINS)

    private val includeLineWrapping = valueOrDefault(INCLUDE_LINE_WRAPPING, false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        val stmt = function.bodyExpression
            ?.singleReturnStatement()
            ?.takeUnless { it.containsReturnStmtsInNullableArguments() }

        if (stmt != null && (includeLineWrapping || !isLineWrapped(stmt))) {
            report(CodeSmell(issue, Entity.from(stmt), issue.description))
        }
    }

    private fun KtExpression.singleReturnStatement(): KtReturnExpression? =
        (this as? KtBlockExpression)?.statements?.singleOrNull() as? KtReturnExpression

    private fun KtReturnExpression.containsReturnStmtsInNullableArguments(): Boolean =
        anyDescendantOfType<KtReturnExpression> { (it.parent as? KtBinaryExpression)?.operationToken == KtTokens.ELVIS }

    private fun isLineWrapped(expression: KtExpression): Boolean =
        expression.children.any { it.text.contains('\n') }

    companion object {
        const val INCLUDE_LINE_WRAPPING = "includeLineWrapping"
    }
}
