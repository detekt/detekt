package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtLambdaExpression

/**
 * Requires nested lambda expressions to be on separate lines.
 *
 * <noncompliant>
 * listOf("")
 *   .map { it.map { it } }
 * </noncompliant>
 *
 * <compliant>
 * listOf("")
 *   .map {
 *     it.map { it }
 *   }
 * </compliant>
 */
class NestedLambdaOnSameLine(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Nested lambda expressions should be on separate lines.",
        debt = Debt.FIVE_MINS,
    )

    override fun visitLambdaExpression(lambdaExpression: KtLambdaExpression) {
        super.visitLambdaExpression(lambdaExpression)

        val body = lambdaExpression.bodyExpression ?: return
        val textBeforeBody = lambdaExpression.text.subSequence(startIndex = 0, endIndex = body.startOffsetInParent)

        if (!textBeforeBody.contains('\n') && body.statements.any { it.containsLambda() }) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(lambdaExpression),
                    message = "Nested lambda expression `${lambdaExpression.bodyExpression?.text}` should be placed " +
                        "on a separate line.",
                )
            )
        }
    }

    private fun PsiElement.containsLambda(): Boolean {
        return this is KtLambdaExpression || children.any { it.containsLambda() }
    }
}
