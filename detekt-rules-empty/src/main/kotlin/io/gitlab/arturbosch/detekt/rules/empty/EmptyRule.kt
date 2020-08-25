package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.hasCommentInside
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Rule to detect empty blocks of code.
 */
@Suppress("detekt.UnnecessaryAbstractClass") // we really do not want instances of this class
abstract class EmptyRule(
    config: Config,
    description: String = "Empty block of code detected. As they serve no purpose they should be removed.",
    private val codeSmellMessage: String = "This empty block of code can be removed."
) : Rule(config) {

    override val issue = Issue(javaClass.simpleName,
            Severity.Minor,
            description,
            Debt.FIVE_MINS)

    fun KtExpression.addFindingIfBlockExprIsEmpty() {
        checkBlockExpr(false)
    }

    fun KtExpression.addFindingIfBlockExprIsEmptyAndNotCommented() {
        checkBlockExpr(true)
    }

    private fun KtExpression.checkBlockExpr(skipIfCommented: Boolean = false) {
        if (this !is KtBlockExpression) return
        val hasComment = hasCommentInside()
        if (skipIfCommented && hasComment) {
            return
        }
        if (children.isEmpty() && !hasComment) {
            report(CodeSmell(issue, Entity.from(this), codeSmellMessage))
        }
    }
}
