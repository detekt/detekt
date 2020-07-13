package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Reports empty `if` blocks. Empty blocks of code serve no purpose and should be removed.
 *
 * @active since v1.0.0
 */
class EmptyIfBlock(config: Config) : EmptyRule(config) {

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        expression.then?.addFindingIfBlockExprIsEmpty() ?: checkThenBodyForLoneSemicolon(expression)
    }

    private fun checkThenBodyForLoneSemicolon(expression: KtIfExpression) {
        val valueOfNextSibling = (expression.nextSibling as? LeafPsiElement)?.elementType as? KtSingleValueToken
        if (valueOfNextSibling?.value?.trim() == ";") {
            report(CodeSmell(issue, Entity.from(expression), "This if block is empty and can be removed."))
        }
    }
}
