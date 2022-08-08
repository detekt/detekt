package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBinaryExpression

/**
 * Unnecessary binary expression add complexity to the code and accomplish nothing. They should be removed.
 * The rule works with all binary expression include if and when condition. The rule also works with all predicates.
 *
 * <noncompliant>
 * val foo = true
 *
 * if (foo || foo) {
 * }
 * </noncompliant>
 *
 * <compliant>
 * val foo = true
 * if (foo) {
 * }
 * </compliant>
 *
 */
class UnnecessaryPartOfBinaryExpression(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UnnecessaryPartOfBinaryExpression",
        Severity.Performance,
        "Detects duplicate condition into binary expression and recommends to remove unnecessary checks",
        Debt.FIVE_MINS
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        val children = expression.children.map { it.text.replace(Regex("\\s"), "") }

        if (children.size != children.distinct().size) {
            report(CodeSmell(issue, Entity.from(expression), issue.description))
        }
    }
}
