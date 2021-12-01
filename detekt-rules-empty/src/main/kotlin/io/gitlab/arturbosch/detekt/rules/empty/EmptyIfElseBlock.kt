package io.gitlab.arturbosch.detekt.rules.empty

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtSingleValueToken
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression

/**
 * Underlying code logic for both [EmptyIfBlock] and [EmptyElseBlock].
 * Both the extending classes have been left as declarations to maintain
 * backwards compatibility.
 */
abstract class EmptyIfElseBlock(
    config: Config,
    private val expressionExtractor: (KtIfExpression) -> KtExpression?
) : EmptyRule(config) {

    protected abstract val blockTypeStr: String

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)
        expressionExtractor(expression)?.addFindingIfBlockExprIsEmpty() ?: checkThenBodyForLoneSemicolon(expression)
    }

    private fun checkThenBodyForLoneSemicolon(expression: KtIfExpression) {
        val valueOfNextSibling = (expression.nextSibling as? LeafPsiElement)?.elementType as? KtSingleValueToken
        if (valueOfNextSibling?.value?.trim() == ";") {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "This $blockTypeStr block is empty and can be removed."
                )
            )
        }
    }
}
