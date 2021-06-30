package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.internal.config
import io.gitlab.arturbosch.detekt.rules.fqNameOrNull
import org.jetbrains.kotlin.lexer.KtTokens.EQEQEQ
import org.jetbrains.kotlin.lexer.KtTokens.EXCLEQEQEQ
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType

/**
 * Kotlin supports two types of equality: structural equality and referential equality. While there are
 * use cases for both, checking for referential equality for some types (such as `String` or `List` is
 * likely not intentional and may case unexpected results.
 *
 * <noncompliant>
 *     val areEqual = "aString" === otherString
 *     val areNotEqual = "aString" !== otherString
 * </noncompliant>
 *
 * <compliant>
 *     val areEqual = "aString" == otherString
 *     val areNotEqual = "aString" != otherString
 * </compliant>
 */
@RequiresTypeResolution
class ReferentialEquality(config: Config) : Rule(config) {

    override val issue = Issue(
        "ReferentialEquality",
        Severity.Warning,
        "",
        Debt.FIVE_MINS
    )

    @Configuration(
        "specifies those types for which referential equality checks are considered a rule violation. " +
            "The types are defined by a list of regular expressions that match the fully qualified type name."
    )
    private val forbiddenTypesRegex: List<Regex> by config(
        listOf(
            "kotlin.String"
        )
    ) { it.map(String::toRegex) }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        checkBinaryExpression(expression)
    }

    private fun checkBinaryExpression(expression: KtBinaryExpression) {
        if (bindingContext == BindingContext.EMPTY) return
        if (expression.operationToken != EQEQEQ && expression.operationToken != EXCLEQEQEQ) return

        val checkedType = expression.left?.getType(bindingContext)?.fqNameOrNull() ?: return
        val fullyQualifiedType = checkedType.asString()

        if (forbiddenTypesRegex.any { it.matches(fullyQualifiedType) }) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(expression),
                    "Checking referential equality may lead to unwanted results."
                )
            )
        }
    }
}
