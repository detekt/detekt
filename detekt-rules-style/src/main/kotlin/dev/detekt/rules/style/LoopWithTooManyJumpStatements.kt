package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtDoWhileExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtLoopExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtWhileExpression

/**
 * Loops which contain multiple `break` or `continue` statements are hard to read and understand.
 * To increase readability they should be refactored into simpler loops.
 *
 * <noncompliant>
 * val strs = listOf("foo, bar")
 * for (str in strs) {
 *     if (str == "bar") {
 *         break
 *     } else {
 *         continue
 *     }
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.2.0")
class LoopWithTooManyJumpStatements(config: Config) : Rule(
    config,
    "The loop contains more than one break or continue statement. " +
        "The code should be refactored to increase readability."
) {

    @Configuration("maximum allowed jumps in a loop")
    private val maxJumpCount: Int by config(1)

    override fun visitLoopExpression(loopExpression: KtLoopExpression) {
        if (countBreakAndReturnStatements(loopExpression.body) > maxJumpCount) {
            report(Finding(Entity.from(loopExpression.keyword ?: loopExpression), description))
        }
        super.visitLoopExpression(loopExpression)
    }

    private fun countBreakAndReturnStatements(body: KtExpression?) = body?.countBreakAndReturnStatementsInLoop() ?: 0

    private fun KtElement.countBreakAndReturnStatementsInLoop(): Int {
        var count = 0
        this.accept(object : DetektVisitor() {
            override fun visitKtElement(element: KtElement) {
                if (element is KtLoopExpression) {
                    return
                }
                if (element is KtBreakExpression || element is KtContinueExpression) {
                    count++
                }
                element.children.forEach { it.accept(this) }
            }
        })
        return count
    }
}

/**
 * For some reason not all keyword properties are exposed on [KtLoopExpression] subclasses, so we have to do it manually.
 */
@Suppress("CommentOverPrivateProperty")
private val KtLoopExpression.keyword: PsiElement?
    get() =
        when (this) {
            is KtForExpression -> this.forKeyword
            is KtWhileExpression -> this.whileKeyword
            is KtDoWhileExpression -> this.doKeyword
            else -> null
        }

private val KtDoWhileExpression.doKeyword: PsiElement?
    get() = KtPsiUtil.findChildByType(this, KtTokens.DO_KEYWORD)

private val KtWhileExpression.whileKeyword: PsiElement?
    get() = KtPsiUtil.findChildByType(this, KtTokens.WHILE_KEYWORD)
