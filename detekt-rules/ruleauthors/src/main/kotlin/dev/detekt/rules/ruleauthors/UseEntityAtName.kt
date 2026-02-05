package dev.detekt.rules.ruleauthors

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression

/**
 * If a rule [report]s issues using [Entity.from] with [KtNamedDeclaration.getNameIdentifier],
 * then it can be replaced with [Entity.atName] for more semantic code and better baseline support.
 */
@ActiveByDefault("1.22.0")
class UseEntityAtName(config: Config) : Rule(config, "Prefer Entity.atName to Entity.from(....nameIdentifier).") {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (isEntityFromCall(expression) && expression.valueArguments.size == 1) {
            val arg = expression.valueArguments.single()
            val target = findNameIdentifierReceiver(arg.getArgumentExpression())
            if (target != null) {
                report(
                    Finding(
                        Entity.from(expression.getCallNameExpression() ?: expression),
                        "Recommended to use Entity.atName(${target.text}) instead."
                    )
                )
            }
        }
    }

    /**
     * @param expression Ideally this method will never get a `null`,
     * because a value argument without a value, or a postfix without a receiver,
     * or a binary expression without a left side, should never really happen.
     * Making [expression] nullable is just a safety measure to be more lenient on code in the wild.
     */
    private fun findNameIdentifierReceiver(expression: KtExpression?): KtExpression? =
        when {
            expression is KtQualifiedExpression ->
                if (expression.selectorExpression?.text == "nameIdentifier") {
                    expression.receiverExpression
                } else {
                    null
                }

            expression is KtPostfixExpression && expression.operationToken == KtTokens.EXCLEXCL ->
                findNameIdentifierReceiver(expression.baseExpression)

            expression is KtBinaryExpression && expression.operationToken == KtTokens.ELVIS ->
                findNameIdentifierReceiver(expression.left)

            else ->
                null
        }

    private fun isEntityFromCall(expression: KtCallExpression): Boolean {
        val callNameExpression = expression.getCallNameExpression()
        return callNameExpression?.text == "from" &&
            callNameExpression.getReceiverExpression()?.text == "Entity"
    }
}
