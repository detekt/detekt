package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtPostfixExpression
import org.jetbrains.kotlin.psi.psiUtil.getCallNameExpression
import org.jetbrains.kotlin.psi.psiUtil.getReceiverExpression
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

/**
 * If a rule reports issues using [Entity.from] with [KtNamedDeclaration.getNameIdentifier],
 * then it can be replaced with [Entity.atName] for more semantic code and better baseline support.
 */
@ActiveByDefault("1.22.0")
class UseNamedLocation(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "UseNamedLocation",
        Severity.Defect,
        "Prefer Entity.atName to Entity.from(....nameIdentifier).",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (isEntityFromCall(expression) && expression.valueArguments.size == 1) {
            val arg = expression.valueArguments.single().getArgumentExpression()!!
            val target = findNameIdentifierReceiver(arg)
            if (target != null) {
                report(
                    CodeSmell(
                        issue,
                        Entity.from(expression.getCallNameExpression()!!),
                        "Recommended to use Entity.atName(${target.text}) instead."
                    )
                )
            }
        }
    }

    private fun findNameIdentifierReceiver(expression: KtExpression): KtExpression? =
        when {
            expression is KtDotQualifiedExpression ->
                if (expression.selectorExpression?.text == "nameIdentifier") {
                    expression.receiverExpression
                } else {
                    null
                }

            expression is KtCallExpression ->
                if (expression.getCallNameExpression()?.text == "nameIdentifier") {
                    expression.referenceExpression()
                } else {
                    null
                }

            expression is KtPostfixExpression && expression.operationToken == KtTokens.EXCLEXCL ->
                findNameIdentifierReceiver(expression.baseExpression!!)

            expression is KtBinaryExpression && expression.operationToken == KtTokens.ELVIS ->
                findNameIdentifierReceiver(expression.left!!)

            else ->
                null
        }

    private fun isEntityFromCall(expression: KtCallExpression): Boolean {
        val callNameExpression = expression.getCallNameExpression()
        return callNameExpression?.text == "from" &&
            callNameExpression.getReceiverExpression()?.text == "Entity"
    }
}
