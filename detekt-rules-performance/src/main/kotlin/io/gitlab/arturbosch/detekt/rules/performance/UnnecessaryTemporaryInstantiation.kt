package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Avoid temporary objects when converting primitive types to String. This has a performance penalty when compared
 * to using primitive types directly.
 * To solve this issue, remove the wrapping type.
 *
 * <noncompliant>
 * val i = Integer(1).toString() // temporary Integer instantiation just for the conversion
 * </noncompliant>
 *
 * <compliant>
 * val i = Integer.toString(1)
 * </compliant>
 *
 * @active since v1.0.0
 */
class UnnecessaryTemporaryInstantiation(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue("UnnecessaryTemporaryInstantiation", Severity.Performance,
            "Avoid temporary objects when converting primitive types to String.",
            Debt.FIVE_MINS)

    private val types: Set<String> = hashSetOf("Boolean", "Byte", "Short", "Integer", "Long", "Float", "Double")

    override fun visitCallExpression(expression: KtCallExpression) {
        if (isPrimitiveWrapperType(expression.calleeExpression) &&
                isToStringMethod(expression.nextSibling?.nextSibling)) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }

    private fun isPrimitiveWrapperType(expression: KtExpression?) = types.contains(expression?.text)

    private fun isToStringMethod(element: PsiElement?) = element?.text == "toString()"
}
