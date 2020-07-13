package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression

/**
 * Instantiation of an object's "empty" state should use the object's "empty" initializer for clarity purposes.
 *
 * <noncompliant>
 * arrayOf()
 * listOf()
 * mapOf()
 * sequenceOf()
 * setOf()
 * </noncompliant>
 *
 * <compliant>
 * emptyArray()
 * emptyList()
 * emptyMap()
 * emptySequence()
 * emptySet()
 * </compliant>
 */
class UseEmptyCounterpart(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        """Instantiation of an object's "empty" state should use the object's "empty" initializer""",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val calleeExpression = expression.calleeExpression ?: return
        val emptyCounterpart = exprsWithEmptyCounterparts[calleeExpression.text]

        if (emptyCounterpart != null && expression.valueArguments.isEmpty()) {
            val message = "${calleeExpression.text} can be replaced with $emptyCounterpart"
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }
}

private val exprsWithEmptyCounterparts = mapOf(
    "arrayOf" to "emptyArray",
    "listOf" to "emptyList",
    "mapOf" to "emptyMap",
    "sequenceOf" to "emptySequence",
    "setOf" to "emptySet"
)
