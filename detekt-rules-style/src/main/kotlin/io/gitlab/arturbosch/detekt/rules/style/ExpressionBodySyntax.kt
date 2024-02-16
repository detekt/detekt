package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
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
 */
class ExpressionBodySyntax(config: Config) : Rule(
    config,
    "Functions with exact one statement, the return statement, can be rewritten with ExpressionBodySyntax."
) {

    @Configuration("include return statements with line wraps in it")
    private val includeLineWrapping: Boolean by config(false)

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        property.getter?.checkForExpressionBodySyntax()
        property.setter?.checkForExpressionBodySyntax()
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        function.checkForExpressionBodySyntax()
    }

    private fun KtDeclarationWithBody.checkForExpressionBodySyntax() {
        val stmt = bodyExpression
            ?.singleReturnStatement()
            ?.takeUnless { it.containsReturnStmtsInNullableArguments() }

        if (stmt != null && (includeLineWrapping || !isLineWrapped(stmt))) {
            report(CodeSmell(Entity.from(stmt), description))
        }
    }

    private fun KtExpression.singleReturnStatement(): KtReturnExpression? =
        (this as? KtBlockExpression)?.statements?.singleOrNull() as? KtReturnExpression

    private fun KtReturnExpression.containsReturnStmtsInNullableArguments(): Boolean =
        anyDescendantOfType<KtReturnExpression> { (it.parent as? KtBinaryExpression)?.operationToken == KtTokens.ELVIS }

    private fun isLineWrapped(expression: KtExpression): Boolean =
        expression.children.any { it.text.contains('\n') }
}
