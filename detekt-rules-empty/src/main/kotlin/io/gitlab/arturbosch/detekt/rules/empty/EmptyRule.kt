package io.gitlab.arturbosch.detekt.rules.empty

import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.empty.internal.hasCommentInside
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Rule to detect empty blocks of code.
 */
@Suppress("AbstractClassCanBeConcreteClass") // we really do not want instances of this class
abstract class EmptyRule(
    config: Config,
    description: String = "Empty block of code detected. As they serve no purpose they should be removed.",
    private val findingMessage: String = "This empty block of code can be removed.",
) : Rule(
    config,
    description
) {

    fun KtExpression.addFindingIfBlockExprIsEmpty() {
        checkBlockExpr(false)
    }

    fun KtExpression.addFindingIfBlockExprIsEmptyAndNotCommented() {
        checkBlockExpr(true)
    }

    protected fun checkThenBodyForLoneSemicolon(expression: KtIfExpression, reportBlock: (KtIfExpression) -> Unit) {
        val valueOfNextSibling = (expression.nextSibling as? LeafPsiElement)?.elementType as? KtSingleValueToken
        if (valueOfNextSibling?.value?.trim() == ";") {
            reportBlock(expression)
        }
    }

    private fun KtExpression.checkBlockExpr(skipIfCommented: Boolean = false) {
        if (this !is KtBlockExpression) return
        val hasComment = hasCommentInside()
        if (skipIfCommented && hasComment) {
            return
        }
        if (children.isEmpty() && !hasComment) {
            report(Finding(Entity.from(this), findingMessage))
        }
    }
}
